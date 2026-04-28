package com.ruoyi.dingtalk.domain.dto;

import lombok.Data;

/**
 * 钉钉部门 DTO。
 *
 * 这里只保留你当前最常用的字段：部门ID、部门名称、父部门ID。
 * 后面如果要同步到 sys_dept，再在这里逐步扩展即可。
 */
@Data
public class DingTalkDeptDto {

    /** 钉钉部门ID */
    private Long deptId;

    /** 部门名称 */
    private String name;

    /** 钉钉父部门ID */
    private Long parentId;
}
