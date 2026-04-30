package com.ruoyi.dingtalk.domain.imports;

import lombok.Data;
import java.util.Date;

/** 导入模板定义表 biz_import_template */
@Data
public class BizImportTemplate {
    private Long id;
    private String templateCode;
    private String templateName;
    private String sheetName;
    private Integer headerRowNo;
    private Integer dataStartRowNo;
    private String targetTable;
    private String status;
    private String remark;
    private String createBy;
    private Date createTime;
    private String updateBy;
    private Date updateTime;
}
