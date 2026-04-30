package com.ruoyi.dingtalk.mapper;

import com.ruoyi.dingtalk.domain.imports.BizImportErrorLog;
import org.apache.ibatis.annotations.Param;
import java.util.List;

public interface BizImportErrorLogMapper {
    List<BizImportErrorLog> selectByBatchId(@Param("batchId") Long batchId);
    int insertError(BizImportErrorLog error);
    int batchInsert(@Param("list") List<BizImportErrorLog> list);
    int deleteByBatchIds(@Param("batchIds") Long[] batchIds);
}
