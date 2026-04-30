package com.ruoyi.dingtalk.domain.imports;

import lombok.Data;
import java.util.Date;

/** 导入批次表 biz_import_batch */
@Data
public class BizImportBatch {
    private Long id;
    private String templateCode;
    private String templateName;
    private String fileName;
    private String filePath;
    private Integer totalCount;
    private Integer successCount;
    private Integer failCount;
    private Integer skipCount;
    private String importStatus;
    private String errorMsg;
    private Date startTime;
    private Date endTime;
    private String createBy;
    private Date createTime;
}
