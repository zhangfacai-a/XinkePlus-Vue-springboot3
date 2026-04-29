package com.ruoyi.dingtalk.domain.vo;

import lombok.Data;

/**
 * 同步结果返回对象。
 */
@Data
public class DingTalkSyncResult {
    /** 新增数量 */
    private int insertCount;

    /** 更新数量 */
    private int updateCount;

    /** 停用数量 */
    private int disabledCount;

    /** 跳过数量 */
    private int skipCount;

    /** 备注信息 */
    private String message;

    public void incInsert() { insertCount++; }
    public void incUpdate() { updateCount++; }
    public void incDisabled() { disabledCount++; }
    public void incSkip() { skipCount++; }
}
