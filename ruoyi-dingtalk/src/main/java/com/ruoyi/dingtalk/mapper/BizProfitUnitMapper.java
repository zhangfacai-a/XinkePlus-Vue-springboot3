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

    /**
     * Excel 导入使用的 upsert。
     * 依赖数据库唯一索引 uk_profit_unit_import：
     * owner_name + start_date + transfer_date + profit_unit_name + deal_type
     */
    int upsertBizProfitUnit(BizProfitUnit entity);

    int updateBizProfitUnit(BizProfitUnit entity);

    int deleteBizProfitUnitByIds(@Param("ids") Long[] ids);

    BizProfitUnit selectByCode(@Param("profitUnitCode") String profitUnitCode);

    BizProfitUnit selectByName(@Param("profitUnitName") String profitUnitName);

    BizProfitUnit selectByCompanyAndName(@Param("companySubjectId") Long companySubjectId,
                                         @Param("profitUnitName") String profitUnitName);

    BizProfitUnit selectByImportUniqueKey(@Param("ownerName") String ownerName,
                                          @Param("startDate") Date startDate,
                                          @Param("transferDate") Date transferDate,
                                          @Param("profitUnitName") String profitUnitName,
                                          @Param("dealType") String dealType);

    /**
     * 判断导入时是否存在日期区间重叠。
     *
     * 判断维度：直接负责人 + 利润单元名称 + 成交类型。
     * 判断规则：老开始日期 <= 新结束日期 且 老结束日期 >= 新开始日期。
     * excludeId 用于编辑时排除当前记录；导入新增前传 null。
     */
    BizProfitUnit selectImportOverlap(@Param("ownerName") String ownerName,
                                      @Param("profitUnitName") String profitUnitName,
                                      @Param("dealType") String dealType,
                                      @Param("startDate") Date startDate,
                                      @Param("transferDate") Date transferDate,
                                      @Param("excludeId") Long excludeId);
}
