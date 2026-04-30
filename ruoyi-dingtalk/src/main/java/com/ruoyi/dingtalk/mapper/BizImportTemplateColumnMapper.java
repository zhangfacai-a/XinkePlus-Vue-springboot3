package com.ruoyi.dingtalk.mapper;

import com.ruoyi.dingtalk.domain.imports.BizImportTemplateColumn;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface BizImportTemplateColumnMapper {
    List<BizImportTemplateColumn> selectByTemplateCode(@Param("templateCode") String templateCode);
}
