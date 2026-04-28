package com.ruoyi.dingtalk.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.dingtalk.service.DingTalkApiService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 钉钉基础接口。
 *
 * 当前模块只保留基础能力：
 * 1. 获取 token
 * 2. 获取部门
 * 3. 获取用户
 *
 * 在线表格相关接口已经全部剥离，后面建议单独重写成：
 * DingTalkSheetController / DingTalkSheetService。
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/dingtalk")
public class DingTalkController {

    private final DingTalkApiService dingTalkApiService;

    /**
     * 获取 access_token。
     *
     * 用途：
     * 1. 测试钉钉应用配置是否正确
     * 2. 测试 Redis token 缓存是否正常
     *
     * CMD 测试：
     * curl -X GET "http://localhost:8080/dingtalk/token"
     */
    @GetMapping("/token")
    public AjaxResult token() {
        return AjaxResult.success(dingTalkApiService.getAccessToken());
    }

    /**
     * 获取企业所有部门。
     *
     * CMD 测试：
     * curl -X GET "http://localhost:8080/dingtalk/depts"
     */
    @GetMapping("/depts")
    public AjaxResult depts() {
        return AjaxResult.success(dingTalkApiService.getAllDepartments());
    }

    /**
     * 获取指定部门下的用户。
     *
     * CMD 测试：
     * curl -X GET "http://localhost:8080/dingtalk/users/by-dept?deptId=1"
     */
    @GetMapping("/users/by-dept")
    public AjaxResult usersByDept(@RequestParam Long deptId) {
        return AjaxResult.success(dingTalkApiService.getUsersByDeptId(deptId));
    }

    /**
     * 获取企业所有用户。
     *
     * 注意：
     * 这个接口会遍历所有部门并拉取用户，企业人数多时会慢。
     *
     * CMD 测试：
     * curl -X GET "http://localhost:8080/dingtalk/users/all"
     */
    @GetMapping("/users/all")
    public AjaxResult usersAll() {
        return AjaxResult.success(dingTalkApiService.getAllUsers());
    }

}
