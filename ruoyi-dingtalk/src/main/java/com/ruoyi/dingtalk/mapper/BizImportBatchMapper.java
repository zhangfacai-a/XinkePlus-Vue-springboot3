package com.ruoyi.dingtalk.mapper;

import com.ruoyi.dingtalk.domain.imports.BizImportBatch;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface BizImportBatchMapper {
    BizImportBatch selectById(Long id);
    List<BizImportBatch> selectList(BizImportBatch query);
    int insertBatch(BizImportBatch batch);
    int updateBatch(BizImportBatch batch);
    int deleteByIds(@Param("ids") Long[] ids);
}
