package com.ruoyi.dingtalk.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.ruoyi.dingtalk.config.DingTalkProperties;
import com.ruoyi.dingtalk.domain.dto.DingTalkDeptDto;
import com.ruoyi.dingtalk.domain.dto.DingTalkUserDto;
import com.ruoyi.dingtalk.exception.DingTalkApiException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.time.Duration;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 钉钉基础 API 服务。
 *
 * 这个类只负责三件事：
 * 1. 获取并缓存 access_token
 * 2. 获取部门
 * 3. 获取用户
 *
 * 注意：
 * 在线表格、业务表导入、Sheet 解析全部不要放在这里。
 * 后面你要重写在线表格模块时，可以单独建 DingTalkSheetService。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DingTalkApiService {

    /** Redis 里的 token 缓存 key */
    private static final String TOKEN_KEY = "dingtalk:access_token";

    /** 钉钉特殊部门ID，旧代码里遇到过，直接跳过 */
    private static final Long SKIP_DEPT_ID = -7L;

    private final DingTalkProperties dingTalkProperties;
    private final StringRedisTemplate stringRedisTemplate;

    private final RestClient restClient = RestClient.builder().build();

    /**
     * 获取钉钉 access_token。
     *
     * 流程：
     * 1. 先从 Redis 读缓存
     * 2. 缓存没有，再调用钉钉 token 接口
     * 3. 成功后写回 Redis，并提前 5 分钟过期
     */
    public String getAccessToken() {
        String cachedToken = stringRedisTemplate.opsForValue().get(TOKEN_KEY);
        if (cachedToken != null && !cachedToken.isBlank()) {
            return cachedToken;
        }

        checkTokenConfig();

        Map<String, Object> body = new HashMap<>();
        body.put("client_id", dingTalkProperties.getClientId());
        body.put("client_secret", dingTalkProperties.getClientSecret());
        body.put("grant_type", "client_credentials");

        JsonNode jsonNode;
        try {
            jsonNode = restClient.post()
                    .uri("https://api.dingtalk.com/v1.0/oauth2/{corpId}/token", dingTalkProperties.getCorpId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (Exception e) {
            throw new DingTalkApiException("调用钉钉 token 接口失败", e);
        }

        if (jsonNode == null || jsonNode.get("access_token") == null) {
            throw new DingTalkApiException("获取钉钉 access_token 失败，返回结果：" + jsonNode);
        }

        String accessToken = jsonNode.get("access_token").asText();
        int expiresIn = jsonNode.path("expires_in").asInt(7200);

        stringRedisTemplate.opsForValue().set(
                TOKEN_KEY,
                accessToken,
                Duration.ofSeconds(Math.max(expiresIn - 300L, 300L))
        );

        log.info("钉钉 access_token 获取成功，已写入 Redis 缓存");
        return accessToken;
    }

    /**
     * 获取所有部门。
     *
     * 采用广度优先遍历：
     * 从 rootDeptId 开始，逐层拉取子部门，直到没有子部门为止。
     */
    public List<DingTalkDeptDto> getAllDepartments() {
        List<DingTalkDeptDto> result = new ArrayList<>();
        Set<Long> visited = new HashSet<>();
        ArrayDeque<Long> queue = new ArrayDeque<>();

        Long rootDeptId = dingTalkProperties.getRootDeptId() == null ? 1L : dingTalkProperties.getRootDeptId();
        queue.add(rootDeptId);

        while (!queue.isEmpty()) {
            Long parentDeptId = queue.poll();
            List<DingTalkDeptDto> children = listSubDepartments(parentDeptId);

            for (DingTalkDeptDto dept : children) {
                if (dept.getDeptId() == null || SKIP_DEPT_ID.equals(dept.getDeptId())) {
                    continue;
                }

                if (visited.add(dept.getDeptId())) {
                    result.add(dept);
                    queue.add(dept.getDeptId());
                }
            }
        }

        return result;
    }

    /**
     * 获取指定部门下的用户列表。
     *
     * 钉钉接口是分页返回的，所以这里用 cursor 循环拉完。
     */
    public List<DingTalkUserDto> getUsersByDeptId(Long deptId) {
        if (deptId == null) {
            throw new DingTalkApiException("deptId 不能为空");
        }

        List<DingTalkUserDto> users = new ArrayList<>();
        long cursor = 0L;
        boolean hasMore = true;

        while (hasMore) {
            String accessToken = getAccessToken();

            Map<String, Object> body = new HashMap<>();
            body.put("dept_id", deptId);
            body.put("cursor", cursor);
            body.put("size", 100);
            body.put("contain_access_limit", false);
            body.put("language", "zh_CN");
            body.put("order_field", "modify_desc");

            JsonNode jsonNode;
            try {
                jsonNode = restClient.post()
                        .uri("https://oapi.dingtalk.com/topapi/v2/user/list?access_token={token}", accessToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(body)
                        .retrieve()
                        .body(JsonNode.class);
            } catch (Exception e) {
                throw new DingTalkApiException("调用钉钉用户列表接口失败，deptId=" + deptId, e);
            }

            checkOldApiResult(jsonNode, "获取部门用户失败，deptId=" + deptId);

            JsonNode resultNode = jsonNode.path("result");
            JsonNode listNode = resultNode.path("list");

            if (listNode.isArray()) {
                for (JsonNode item : listNode) {
                    users.add(parseUser(item));
                }
            }

            hasMore = resultNode.path("has_more").asBoolean(false);
            cursor = parseNextCursor(resultNode.get("next_cursor"));
        }

        return users;
    }

    /**
     * 获取企业全部用户。
     *
     * 逻辑：
     * 1. 先获取所有部门
     * 2. 再按部门拉用户
     * 3. 因为一个员工可能在多个部门，所以用 userid 去重
     */
    public List<DingTalkUserDto> getAllUsers() {
        List<DingTalkDeptDto> allDepartments = getAllDepartments();
        Map<String, DingTalkUserDto> userMap = new LinkedHashMap<>();

        Long rootDeptId = dingTalkProperties.getRootDeptId() == null ? 1L : dingTalkProperties.getRootDeptId();
        List<Long> deptIds = new ArrayList<>();
        deptIds.add(rootDeptId);

        for (DingTalkDeptDto dept : allDepartments) {
            if (dept.getDeptId() != null) {
                deptIds.add(dept.getDeptId());
            }
        }

        for (Long deptId : deptIds) {
            List<DingTalkUserDto> deptUsers = getUsersByDeptId(deptId);
            for (DingTalkUserDto user : deptUsers) {
                if (user.getUserid() == null || user.getUserid().isBlank()) {
                    continue;
                }
                userMap.putIfAbsent(user.getUserid(), user);
            }
        }

        return new ArrayList<>(userMap.values());
    }

    /**
     * 获取某个部门的直属子部门。
     */
    private List<DingTalkDeptDto> listSubDepartments(Long deptId) {
        String accessToken = getAccessToken();

        Map<String, Object> body = Map.of(
                "dept_id", deptId,
                "language", "zh_CN"
        );

        JsonNode jsonNode;
        try {
            jsonNode = restClient.post()
                    .uri("https://oapi.dingtalk.com/topapi/v2/department/listsub?access_token={token}", accessToken)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(body)
                    .retrieve()
                    .body(JsonNode.class);
        } catch (Exception e) {
            throw new DingTalkApiException("调用钉钉子部门接口失败，deptId=" + deptId, e);
        }

        checkOldApiResult(jsonNode, "获取子部门失败，deptId=" + deptId);

        List<DingTalkDeptDto> list = new ArrayList<>();
        JsonNode resultNode = jsonNode.path("result");

        if (resultNode.isArray()) {
            for (JsonNode item : resultNode) {
                DingTalkDeptDto dto = new DingTalkDeptDto();
                dto.setDeptId(item.path("dept_id").isMissingNode() ? null : item.path("dept_id").asLong());
                dto.setName(item.path("name").asText(null));
                dto.setParentId(item.path("parent_id").isMissingNode() ? null : item.path("parent_id").asLong());
                list.add(dto);
            }
        }

        return list;
    }

    /**
     * 解析用户 JSON。
     */
    private DingTalkUserDto parseUser(JsonNode item) {
        DingTalkUserDto dto = new DingTalkUserDto();
        dto.setUserid(item.path("userid").asText(null));
        dto.setUnionid(item.path("unionid").asText(null));
        dto.setName(item.path("name").asText(null));
        dto.setMobile(item.path("mobile").asText(null));
        dto.setJobNumber(item.path("job_number").asText(null));
        dto.setTitle(item.path("title").asText(null));
        dto.setEmail(item.path("email").asText(null));

        List<Long> deptIdList = new ArrayList<>();
        JsonNode deptIdNode = item.path("dept_id_list");
        if (deptIdNode.isArray()) {
            for (JsonNode dept : deptIdNode) {
                deptIdList.add(dept.asLong());
            }
        }
        dto.setDeptIdList(deptIdList);

        return dto;
    }

    /**
     * 钉钉旧版 oapi 接口统一检查。
     */
    private void checkOldApiResult(JsonNode jsonNode, String message) {
        if (jsonNode == null || jsonNode.path("errcode").asInt(-1) != 0) {
            throw new DingTalkApiException(message + "，返回结果：" + jsonNode);
        }
    }

    /**
     * 解析分页游标。
     */
    private long parseNextCursor(JsonNode nextCursorNode) {
        if (nextCursorNode == null || nextCursorNode.isNull()) {
            return 0L;
        }
        if (nextCursorNode.isTextual()) {
            return Long.parseLong(nextCursorNode.asText("0"));
        }
        return nextCursorNode.asLong(0L);
    }

    /**
     * 检查 token 必需配置，避免空配置时排查半天。
     */
    private void checkTokenConfig() {
        if (isBlank(dingTalkProperties.getClientId())) {
            throw new DingTalkApiException("dingtalk.client-id 未配置");
        }
        if (isBlank(dingTalkProperties.getClientSecret())) {
            throw new DingTalkApiException("dingtalk.client-secret 未配置");
        }
        if (isBlank(dingTalkProperties.getCorpId())) {
            throw new DingTalkApiException("dingtalk.corp-id 未配置");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
