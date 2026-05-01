package com.ruoyi.dingtalk.service.biz;

import com.ruoyi.dingtalk.domain.biz.BizBudgetMonth;
import com.ruoyi.dingtalk.mapper.BizBudgetMonthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

/** BudgetMonth 业务服务 */
@Service
@RequiredArgsConstructor
public class BizBudgetMonthService {
    private final BizBudgetMonthMapper mapper;

    public BizBudgetMonth getById(Long id) { return mapper.selectBizBudgetMonthById(id); }
    public List<BizBudgetMonth> list(BizBudgetMonth query) { return mapper.selectBizBudgetMonthList(query); }
    public int add(BizBudgetMonth entity) { return mapper.insertBizBudgetMonth(entity); }
    public int update(BizBudgetMonth entity) { return mapper.updateBizBudgetMonth(entity); }
    public int delete(Long[] ids) { return mapper.deleteBizBudgetMonthByIds(ids); }
}
