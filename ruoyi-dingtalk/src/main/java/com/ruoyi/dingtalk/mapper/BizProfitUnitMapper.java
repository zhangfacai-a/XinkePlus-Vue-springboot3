package com.ruoyi.dingtalk.mapper;

import com.ruoyi.dingtalk.domain.biz.BizProfitUnit;
import org.apache.ibatis.annotations.Param;

import java.util.Date;
import java.util.List;

/**
 * 利润单元 Mapper
 */
public interface BizProfitUnitMapper {

    BizProfitUnit selectBizProfitUnitById(Long id);

    List<BizProfitUnit> selectBizProfitUnitList(BizProfitUnit query);

    int insertBizProfitUnit(BizProfitUnit entity);

    int updateBizProfitUnit(BizProfitUnit entity);

    int deleteBizProfitUnitByIds(@Param("ids") Long[] ids);

    BizProfitUnit selectByCode(@Param("profitUnitCode") String profitUnitCode);

    BizProfitUnit selectByName(@Param("profitUnitName") String profitUnitName);

    BizProfitUnit selectByCompanyAndName(@Param("companySubjectId") Long companySubjectId,
                                         @Param("profitUnitName") String profitUnitName);

    /**
     * 判断同一利润单元 + 成交类型，日期区间是否重叠
     */
    BizProfitUnit selectOverlap(@Param("profitUnitName") String profitUnitName,
                                @Param("dealType") String dealType,
                                @Param("startDate") Date startDate,
                                @Param("transferDate") Date transferDate,
                                @Param("excludeId") Long excludeId);
}