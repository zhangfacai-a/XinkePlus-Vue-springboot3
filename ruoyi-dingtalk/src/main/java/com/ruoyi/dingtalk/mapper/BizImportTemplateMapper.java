package com.ruoyi.dingtalk.mapper;

import com.ruoyi.dingtalk.domain.imports.BizImportTemplate;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface BizImportTemplateMapper {
    BizImportTemplate selectByCode(@Param("templateCode") String templateCode);
    List<BizImportTemplate> selectList(BizImportTemplate query);
}
