package com.ruoyi.dingtalk.service.imports.handler;

import com.ruoyi.dingtalk.domain.biz.BizProfitUnit;
import com.ruoyi.dingtalk.domain.biz.BizTargetYear;
import com.ruoyi.dingtalk.mapper.BizProfitUnitMapper;
import com.ruoyi.dingtalk.mapper.BizTargetYearMapper;
import com.ruoyi.dingtalk.service.imports.ImportHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import static com.ruoyi.dingtalk.service.imports.ImportValueUtils.*;

/** 年度目标导入 */
@Component
@RequiredArgsConstructor
public class TargetYearImportHandler implements ImportHandler {
    private final BizTargetYearMapper mapper;
    private final BizProfitUnitMapper profitUnitMapper;
    @Override public String supportTemplateCode() { return "TARGET_YEAR"; }

    @Override
    public void importRow(Map<String, Object> row, Integer rowNo, Long batchId) {
        Integer year = integer(row, "year");
        if (year == null) throw new IllegalArgumentException("年份不能为空");
        String profitUnitName = required(row, "profit_unit_name", "利润单元名称");
        BizProfitUnit unit = profitUnitMapper.selectByName(profitUnitName);
        BizTargetYear entity = mapper.selectByYearAndProfitUnit(year, unit == null ? null : unit.getId(), profitUnitName);
        if (entity == null) entity = new BizTargetYear();
        entity.setYear(year);
        entity.setProfitUnitName(profitUnitName);
        if (unit != null) {
            entity.setProfitUnitId(unit.getId());
            entity.setCompanySubjectId(unit.getCompanySubjectId());
            entity.setPlatform(unit.getPlatform());
            entity.setBrandName(unit.getBrandName());
            entity.setDeptName(unit.getDeptName());
            entity.setOwnerName(unit.getOwnerName());
        }
        entity.setCompanyName(str(row, "company_name"));
        entity.setSubjectName(str(row, "subject_name"));
        entity.setGmvTarget(decimal(row, "gmv_target"));
        entity.setSalesTarget(decimal(row, "sales_target"));
        entity.setProfitTarget(decimal(row, "profit_target"));
        entity.setCostTarget(decimal(row, "cost_target"));
        entity.setAdCostTarget(decimal(row, "ad_cost_target"));
        entity.setRoiTarget(decimal(row, "roi_target"));
        entity.setProfitRateTarget(decimal(row, "profit_rate_target"));
        entity.setRemark(str(row, "remark"));
        entity.setStatus("0");
        if (entity.getId() == null) { entity.setCreateBy("excel_import"); mapper.insertBizTargetYear(entity); }
        else { entity.setUpdateBy("excel_import"); mapper.updateBizTargetYear(entity); }
    }
}
