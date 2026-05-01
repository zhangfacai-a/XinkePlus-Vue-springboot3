package com.ruoyi.dingtalk.service.biz;

import com.ruoyi.dingtalk.domain.biz.BizTargetYear;
import com.ruoyi.dingtalk.mapper.BizTargetYearMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

/** TargetYear 业务服务 */
@Service
@RequiredArgsConstructor
public class BizTargetYearService {
    private final BizTargetYearMapper mapper;

    public BizTargetYear getById(Long id) { return mapper.selectBizTargetYearById(id); }
    public List<BizTargetYear> list(BizTargetYear query) { return mapper.selectBizTargetYearList(query); }
    public int add(BizTargetYear entity) { return mapper.insertBizTargetYear(entity); }
    public int update(BizTargetYear entity) { return mapper.updateBizTargetYear(entity); }
    public int delete(Long[] ids) { return mapper.deleteBizTargetYearByIds(ids); }
}
