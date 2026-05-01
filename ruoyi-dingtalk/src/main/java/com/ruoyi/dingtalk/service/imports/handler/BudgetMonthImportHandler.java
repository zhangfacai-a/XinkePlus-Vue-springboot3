package com.ruoyi.dingtalk.service.imports.handler;

import com.ruoyi.dingtalk.domain.biz.BizBudgetMonth;
import com.ruoyi.dingtalk.domain.biz.BizProfitUnit;
import com.ruoyi.dingtalk.mapper.BizBudgetMonthMapper;
import com.ruoyi.dingtalk.mapper.BizProfitUnitMapper;
import com.ruoyi.dingtalk.service.imports.ImportHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Map;
import static com.ruoyi.dingtalk.service.imports.ImportValueUtils.*;

/** 月度预算导入 */
@Component
@RequiredArgsConstructor
public class BudgetMonthImportHandler implements ImportHandler {
    private final BizBudgetMonthMapper mapper;
    private final BizProfitUnitMapper profitUnitMapper;
    @Override public String supportTemplateCode() { return "BUDGET_MONTH"; }

    @Override
    public void importRow(Map<String, Object> row, Integer rowNo, Long batchId) {
        Integer year = integer(row, "year");
        Integer month = integer(row, "month");
        if (year == null) throw new IllegalArgumentException("年份不能为空");
        if (month == null || month < 1 || month > 12) throw new IllegalArgumentException("月份必须是1-12");
        String profitUnitName = required(row, "profit_unit_name", "利润单元名称");
        BizProfitUnit unit = profitUnitMapper.selectByName(profitUnitName);
        BizBudgetMonth entity = mapper.selectByYearMonthAndProfitUnit(year, month, unit == null ? null : unit.getId(), profitUnitName);
        if (entity == null) entity = new BizBudgetMonth();
        entity.setYear(year);
        entity.setMonth(month);
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
        entity.setGmvBudget(decimal(row, "gmv_budget"));
        entity.setSalesBudget(decimal(row, "sales_budget"));
        entity.setProfitBudget(decimal(row, "profit_budget"));
        entity.setCostBudget(decimal(row, "cost_budget"));
        entity.setAdCostBudget(decimal(row, "ad_cost_budget"));
        entity.setRoiBudget(decimal(row, "roi_budget"));
        entity.setProfitRateBudget(decimal(row, "profit_rate_budget"));
        entity.setRemark(str(row, "remark"));
        entity.setStatus("0");
        if (entity.getId() == null) { entity.setCreateBy("excel_import"); mapper.insertBizBudgetMonth(entity); }
        else { entity.setUpdateBy("excel_import"); mapper.updateBizBudgetMonth(entity); }
    }
}
