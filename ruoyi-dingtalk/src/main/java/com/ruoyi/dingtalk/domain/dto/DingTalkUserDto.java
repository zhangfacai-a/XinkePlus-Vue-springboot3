package com.ruoyi.dingtalk.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 钉钉用户 DTO。
 *
 * 注意：这是钉钉用户，不是若依 SysUser。
 */
@Data
public class DingTalkUserDto {
    /** 钉钉 userid，企业内唯一，是同步 sys_user 的核心唯一键 */
    private String userid;

    /** unionid，跨应用场景使用 */
    private String unionid;

    /** 员工姓名 */
    private String name;

    /** 手机号 */
    private String mobile;

    /** 工号 */
    private String jobNumber;

    /** 职位 */
    private String title;

    /** 邮箱 */
    private String email;

    /** 所属部门ID列表，取第一个作为主部门 */
    private List<Long> deptIdList;
}
