package com.ruoyi.dingtalk.domain.imports;

import lombok.Data;
import java.util.Date;

/** 导入错误日志表 biz_import_error_log */
@Data
public class BizImportErrorLog {
    private Long id;
    private Long batchId;
    private String sheetName;
    private Integer rowNo;
    private String columnName;
    private String fieldName;
    private String rawValue;
    private String errorType;
    private String errorMsg;
    private Date createTime;
}
