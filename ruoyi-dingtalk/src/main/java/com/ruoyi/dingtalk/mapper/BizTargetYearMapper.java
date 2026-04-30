package com.ruoyi.dingtalk.mapper;

import com.ruoyi.dingtalk.domain.biz.BizTargetYear;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/** 年度目标 Mapper */
public interface BizTargetYearMapper {
    BizTargetYear selectBizTargetYearById(Long id);
    List<BizTargetYear> selectBizTargetYearList(BizTargetYear query);
    int insertBizTargetYear(BizTargetYear entity);
    int updateBizTargetYear(BizTargetYear entity);
    int deleteBizTargetYearByIds(@Param("ids") Long[] ids);

    BizTargetYear selectByYearAndProfitUnit(@Param("year") Integer year, @Param("profitUnitId") Long profitUnitId, @Param("profitUnitName") String profitUnitName);
}
