package com.ruoyi.dingtalk.domain.imports;

import lombok.Data;

/** 上传导入返回结果 */
@Data
public class BizImportResult {
    private Long batchId;
    private Integer totalCount = 0;
    private Integer successCount = 0;
    private Integer failCount = 0;
    private Integer skipCount = 0;
    private String importStatus;
    private String message;
}
