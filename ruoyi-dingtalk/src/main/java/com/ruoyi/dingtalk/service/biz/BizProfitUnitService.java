package com.ruoyi.dingtalk.service.biz;

import com.ruoyi.dingtalk.domain.biz.BizProfitUnit;
import com.ruoyi.dingtalk.mapper.BizProfitUnitMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

/** ProfitUnit 业务服务 */
@Service
@RequiredArgsConstructor
public class BizProfitUnitService {
    private final BizProfitUnitMapper mapper;

    public BizProfitUnit getById(Long id) { return mapper.selectBizProfitUnitById(id); }
    public List<BizProfitUnit> list(BizProfitUnit query) { return mapper.selectBizProfitUnitList(query); }
    public int add(BizProfitUnit entity) { return mapper.insertBizProfitUnit(entity); }
    public int update(BizProfitUnit entity) { return mapper.updateBizProfitUnit(entity); }
    public int delete(Long[] ids) { return mapper.deleteBizProfitUnitByIds(ids); }
}
