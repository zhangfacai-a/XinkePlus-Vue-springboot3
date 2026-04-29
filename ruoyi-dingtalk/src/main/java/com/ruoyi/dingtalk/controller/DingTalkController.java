package com.ruoyi.dingtalk.controller;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.dingtalk.service.DingTalkApiService;
import com.ruoyi.dingtalk.service.DingTalkSyncService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 钉钉基础接口。
 *
 * 当前模块只做：
 * 1. token 测试
 * 2. 部门/用户读取测试
 * 3. 部门/用户同步到若依
 *
 * 在线表格、Sheet 导入不要写在这里，后面单独重写。
 */
@RestController
@RequestMapping("/dingtalk")
@RequiredArgsConstructor
public class DingTalkController {

    private final DingTalkApiService dingTalkApiService;
    private final DingTalkSyncService dingTalkSyncService;

    /**
     * 测试钉钉 token 是否能正常获取。
     *
     * CMD：
     * curl http://localhost:8080/dingtalk/token
     */
    @GetMapping("/token")
    public AjaxResult token() {
        return AjaxResult.success(dingTalkApiService.getAccessToken());
    }

    /**
     * 测试拉取钉钉全部部门。
     *
     * CMD：
     * curl http://localhost:8080/dingtalk/depts
     */
    @GetMapping("/depts")
    public AjaxResult depts() {
        return AjaxResult.success(dingTalkApiService.getAllDepartments());
    }

    /**
     * 测试拉取指定部门用户。
     *
     * CMD：
     * curl "http://localhost:8080/dingtalk/users/by-dept?deptId=1"
     */
    @GetMapping("/users/by-dept")
    public AjaxResult usersByDept(@RequestParam Long deptId) {
        return AjaxResult.success(dingTalkApiService.getUsersByDeptId(deptId));
    }

    /**
     * 测试拉取钉钉全部在职用户。
     *
     * CMD：
     * curl http://localhost:8080/dingtalk/users/all
     */
    @GetMapping("/users/all")
    public AjaxResult usersAll() {
        return AjaxResult.success(dingTalkApiService.getAllUsers());
    }

    /**
     * 同步部门到若依 sys_dept。
     *
     * CMD：
     * curl -X POST http://localhost:8080/dingtalk/sync/depts
     */
    @PostMapping("/sync/depts")
    public AjaxResult syncDepts() {
        return AjaxResult.success(dingTalkSyncService.syncDepartments());
    }

    /**
     * 同步用户到若依 sys_user。
     *
     * 注意：同步用户前，建议先同步部门。
     *
     * CMD：
     * curl -X POST http://localhost:8080/dingtalk/sync/users
     */
    @PostMapping("/sync/users")
    public AjaxResult syncUsers() {
        return AjaxResult.success(dingTalkSyncService.syncUsers());
    }

    /**
     * 一键同步：先部门，后用户。
     *
     * CMD：
     * curl -X POST http://localhost:8080/dingtalk/sync/all
     */
    @PostMapping("/sync/all")
    public AjaxResult syncAll() {
        return AjaxResult.success(dingTalkSyncService.syncAll());
    }

    /**
     * 手动标记离职。
     *
     * 只停用用户，不删除用户。
     *
     * CMD 示例：
     * curl -X POST "http://localhost:8080/dingtalk/sync/dimission" ^
     *   -H "Content-Type: application/json" ^
     *   -d "[\"userid001\",\"userid002\"]"
     */
    @PostMapping("/sync/dimission")
    public AjaxResult markDimission(@RequestBody List<String> dingTalkUserIds) {
        return AjaxResult.success(dingTalkSyncService.markDimission(dingTalkUserIds));
    }
}
