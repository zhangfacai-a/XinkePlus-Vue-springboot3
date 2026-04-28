package com.ruoyi.dingtalk.domain.dto;

import lombok.Data;

import java.util.List;

/**
 * 钉钉用户 DTO。
 *
 * 注意：
 * 1. 这是从钉钉接口返回的数据对象，不是若依 SysUser。
 * 2. 不要在这里混入数据库字段，避免后面重构困难。
 */
@Data
public class DingTalkUserDto {

    /** 钉钉用户ID，企业内唯一，后续同步用户时最重要 */
    private String userid;

    /** 钉钉 unionid，跨应用场景可能会用到 */
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

    /** 所属部门ID列表 */
    private List<Long> deptIdList;
}
