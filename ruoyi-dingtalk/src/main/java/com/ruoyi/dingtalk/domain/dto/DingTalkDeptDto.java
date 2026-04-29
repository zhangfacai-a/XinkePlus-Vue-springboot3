package com.ruoyi.dingtalk.domain.dto;

import lombok.Data;

/**
 * 钉钉部门 DTO。
 *
 * 只表示钉钉接口返回的数据，不直接等同于 sys_dept。
 */
@Data
public class DingTalkDeptDto {
    /** 钉钉部门ID */
    private Long deptId;

    /** 钉钉父部门ID */
    private Long parentId;

    /** 部门名称 */
    private String name;
}
