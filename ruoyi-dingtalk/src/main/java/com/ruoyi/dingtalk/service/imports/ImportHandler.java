package com.ruoyi.dingtalk.service.imports;

import java.util.Map;

/**
 * 导入处理器。
 * 模板配置只解决“Excel列 -> 字段名”，真正的业务校验和关联查询放在 Handler 里。
 */
public interface ImportHandler {
    /** 支持的模板编码，例如 PROFIT_UNIT */
    String supportTemplateCode();

    /** 导入一行。实现类需要自己处理新增/更新。 */
    void importRow(Map<String, Object> row, Integer rowNo, Long batchId);
}
