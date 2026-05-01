package com.ruoyi.dingtalk.mapper;

import com.ruoyi.dingtalk.domain.biz.BizBudgetMonth;
import org.apache.ibatis.annotations.Param;
import java.util.List;

/** 月度预算 Mapper */
public interface BizBudgetMonthMapper {
    BizBudgetMonth selectBizBudgetMonthById(Long id);
    List<BizBudgetMonth> selectBizBudgetMonthList(BizBudgetMonth query);
    int insertBizBudgetMonth(BizBudgetMonth entity);
    int updateBizBudgetMonth(BizBudgetMonth entity);
    int deleteBizBudgetMonthByIds(@Param("ids") Long[] ids);

    BizBudgetMonth selectByYearMonthAndProfitUnit(@Param("year") Integer year, @Param("month") Integer month, @Param("profitUnitId") Long profitUnitId, @Param("profitUnitName") String profitUnitName);
}
