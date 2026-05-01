package com.ruoyi.dingtalk.domain.imports;

import lombok.Data;

/** 导入模板字段映射表 biz_import_template_column */
@Data
public class BizImportTemplateColumn {
    private Long id;
    private String templateCode;
    private String excelColumn;
    private String dbField;
    private String fieldType;
    private String requiredFlag;
    private String defaultValue;
    private String formatRule;
    private Integer sortOrder;
}
