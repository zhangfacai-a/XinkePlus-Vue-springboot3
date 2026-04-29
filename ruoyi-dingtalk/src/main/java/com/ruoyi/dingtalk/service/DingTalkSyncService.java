package com.ruoyi.dingtalk.service;

import com.ruoyi.dingtalk.config.DingTalkProperties;
import com.ruoyi.dingtalk.domain.dto.DingTalkDeptDto;
import com.ruoyi.dingtalk.domain.dto.DingTalkUserDto;
import com.ruoyi.dingtalk.domain.vo.DingTalkSyncResult;
import com.ruoyi.dingtalk.mapper.DingTalkSyncMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 钉钉同步到若依的核心服务。
 *
 * 主线：
 * 1. 同步部门到 sys_dept
 * 2. 同步在职用户到 sys_user
 * 3. 对连续多次没出现的钉钉用户做停用处理
 *
 * 离职策略：
 * - 不删除 sys_user
 * - 只把 status 改为 '1' 停用
 * - del_flag 保持 '0'
 * - 保留 dingtalk_user_id、nick_name、dept_id、dimission_time 等历史痕迹
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DingTalkSyncService {

    private static final String SYSTEM_OPERATOR = "dingtalk_sync";

    private final DingTalkApiService dingTalkApiService;
    private final DingTalkSyncMapper dingTalkSyncMapper;
    private final DingTalkProperties dingTalkProperties;
    private final PasswordEncoder passwordEncoder;

    /** 同步部门 + 用户。必须先同步部门，因为用户需要部门映射。 */
    @Transactional(rollbackFor = Exception.class)
    public Map<String, DingTalkSyncResult> syncAll() {
        DingTalkSyncResult deptResult = syncDepartments();
        DingTalkSyncResult userResult = syncUsers();
        Map<String, DingTalkSyncResult> result = new HashMap<>();
        result.put("dept", deptResult);
        result.put("user", userResult);
        return result;
    }

    /** 同步钉钉部门到若依 sys_dept。 */
    @Transactional(rollbackFor = Exception.class)
    public DingTalkSyncResult syncDepartments() {
        List<DingTalkDeptDto> departments = dingTalkApiService.getAllDepartments();
        DingTalkSyncResult result = new DingTalkSyncResult();

        if (departments == null || departments.isEmpty()) {
            result.setMessage("钉钉没有返回子部门，本次未更新部门");
            return result;
        }

        // 父部门必须先处理，否则子部门找不到 parent_id。
        departments.sort(Comparator.comparing(DingTalkDeptDto::getParentId, Comparator.nullsFirst(Long::compareTo)));

        List<Long> currentDingTalkDeptIds = new ArrayList<>();
        for (DingTalkDeptDto dept : departments) {
            if (dept.getDeptId() == null || isBlank(dept.getName())) {
                result.incSkip();
                continue;
            }
            currentDingTalkDeptIds.add(dept.getDeptId());

            Long ruoyiParentId = resolveRuoyiParentDeptId(dept.getParentId());
            String ancestors = buildAncestors(ruoyiParentId);

            Map<String, Object> exists = dingTalkSyncMapper.selectDeptByDingTalkDeptId(dept.getDeptId());
            if (exists == null) {
                dingTalkSyncMapper.insertDept(ruoyiParentId, ancestors, dept.getName(), 0, dept.getDeptId(), SYSTEM_OPERATOR);
                result.incInsert();
            } else {
                Long deptId = toLong(exists.get("dept_id"));
                dingTalkSyncMapper.updateDept(deptId, ruoyiParentId, ancestors, dept.getName(), 0, "0", SYSTEM_OPERATOR);
                result.incUpdate();
            }
        }

        int disabled = dingTalkSyncMapper.disableDeptNotIn(currentDingTalkDeptIds, SYSTEM_OPERATOR);
        for (int i = 0; i < disabled; i++) result.incDisabled();
        result.setMessage("部门同步完成；不存在于本次钉钉返回结果的部门已停用，不删除");
        return result;
    }

    /** 同步钉钉在职用户到若依 sys_user。 */
    @Transactional(rollbackFor = Exception.class)
    public DingTalkSyncResult syncUsers() {
        List<DingTalkUserDto> users = dingTalkApiService.getAllUsers();
        DingTalkSyncResult result = new DingTalkSyncResult();
        LocalDateTime now = LocalDateTime.now();

        if (users == null || users.isEmpty()) {
            result.setMessage("钉钉没有返回在职用户，本次未更新用户，避免误停用");
            return result;
        }

        List<String> activeUserIds = new ArrayList<>();
        Set<String> seen = new HashSet<>();

        for (DingTalkUserDto user : users) {
            if (user == null || isBlank(user.getUserid())) {
                result.incSkip();
                continue;
            }
            if (!seen.add(user.getUserid())) {
                result.incSkip();
                continue;
            }
            activeUserIds.add(user.getUserid());

            Long dingTalkDeptId = resolvePrimaryDingTalkDeptId(user);
            Long ruoyiDeptId = resolveRuoyiDeptIdForUser(dingTalkDeptId);
            Map<String, Object> exists = dingTalkSyncMapper.selectUserByDingTalkUserId(user.getUserid());

            if (exists == null) {
                dingTalkSyncMapper.insertUser(
                        ruoyiDeptId,
                        buildUserName(user),
                        safeName(user),
                        emptyToNull(user.getEmail()),
                        emptyToNull(user.getMobile()),
                        passwordEncoder.encode(dingTalkProperties.getDefaultPassword()),
                        user.getUserid(),
                        emptyToNull(user.getUnionid()),
                        dingTalkDeptId,
                        emptyToNull(user.getJobNumber()),
                        emptyToNull(user.getTitle()),
                        now,
                        SYSTEM_OPERATOR
                );
                result.incInsert();
            } else {
                Long userId = toLong(exists.get("user_id"));
                dingTalkSyncMapper.updateUserActive(
                        userId,
                        ruoyiDeptId,
                        safeName(user),
                        emptyToNull(user.getEmail()),
                        emptyToNull(user.getMobile()),
                        emptyToNull(user.getUnionid()),
                        dingTalkDeptId,
                        emptyToNull(user.getJobNumber()),
                        emptyToNull(user.getTitle()),
                        now,
                        SYSTEM_OPERATOR
                );
                result.incUpdate();
            }
        }

        // 没在本次在职接口出现的用户，不马上当离职，只增加 missing_count。
        dingTalkSyncMapper.increaseMissingCountForAbsentUsers(activeUserIds, now, SYSTEM_OPERATOR);
        int disabled = dingTalkSyncMapper.disableMissingUsers(dingTalkProperties.getMissingThreshold(), now, SYSTEM_OPERATOR);
        for (int i = 0; i < disabled; i++) result.incDisabled();
        result.setMessage("用户同步完成；新增/更新在职用户；连续多次未出现的用户已停用但未删除");
        return result;
    }

    /** 手动标记离职：只停用，不删除。后面接钉钉离职接口时也可以复用它。 */
    @Transactional(rollbackFor = Exception.class)
    public DingTalkSyncResult markDimission(List<String> dingTalkUserIds) {
        DingTalkSyncResult result = new DingTalkSyncResult();
        if (dingTalkUserIds == null || dingTalkUserIds.isEmpty()) {
            result.setMessage("未传入钉钉 userid");
            return result;
        }
        LocalDateTime now = LocalDateTime.now();
        for (String dingTalkUserId : dingTalkUserIds) {
            if (isBlank(dingTalkUserId)) {
                result.incSkip();
                continue;
            }
            int count = dingTalkSyncMapper.markUserDimission(dingTalkUserId, now, SYSTEM_OPERATOR);
            if (count > 0) result.incDisabled(); else result.incSkip();
        }
        result.setMessage("离职用户已停用，sys_user.del_flag 未改，历史数据保留");
        return result;
    }

    private Long resolveRuoyiParentDeptId(Long dingTalkParentDeptId) {
        if (dingTalkParentDeptId == null || Objects.equals(dingTalkParentDeptId, dingTalkProperties.getRootDeptId())) {
            return dingTalkProperties.getRuoyiRootDeptId();
        }
        Long ruoyiParentId = dingTalkSyncMapper.selectDeptIdByDingTalkDeptId(dingTalkParentDeptId);
        return ruoyiParentId == null ? dingTalkProperties.getRuoyiRootDeptId() : ruoyiParentId;
    }

    private String buildAncestors(Long parentDeptId) {
        if (parentDeptId == null || parentDeptId <= 0) return "0";
        Map<String, Object> parent = dingTalkSyncMapper.selectDeptByDeptId(parentDeptId);
        if (parent == null) return "0";
        String parentAncestors = String.valueOf(parent.getOrDefault("ancestors", "0"));
        return parentAncestors + "," + parentDeptId;
    }

    private Long resolvePrimaryDingTalkDeptId(DingTalkUserDto user) {
        List<Long> deptIdList = user.getDeptIdList() == null ? Collections.emptyList() : user.getDeptIdList();
        if (deptIdList.isEmpty()) return dingTalkProperties.getRootDeptId();
        return deptIdList.get(0);
    }

    private Long resolveRuoyiDeptIdForUser(Long dingTalkDeptId) {
        if (dingTalkDeptId == null || Objects.equals(dingTalkDeptId, dingTalkProperties.getRootDeptId())) {
            return dingTalkProperties.getRuoyiRootDeptId();
        }
        Long deptId = dingTalkSyncMapper.selectDeptIdByDingTalkDeptId(dingTalkDeptId);
        return deptId == null ? dingTalkProperties.getRuoyiRootDeptId() : deptId;
    }

    private String buildUserName(DingTalkUserDto user) {
        return "dt_" + user.getUserid();
    }

    private String safeName(DingTalkUserDto user) {
        return !isBlank(user.getName()) ? user.getName() : user.getUserid();
    }

    private String emptyToNull(String value) {
        return isBlank(value) ? null : value;
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private Long toLong(Object value) {
        if (value == null) return null;
        if (value instanceof Number number) return number.longValue();
        return Long.parseLong(String.valueOf(value));
    }
}
