package com.ruoyi.dingtalk.service.biz;

import com.ruoyi.dingtalk.domain.biz.BizStoreAuth;
import com.ruoyi.dingtalk.mapper.BizStoreAuthMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

/** StoreAuth 业务服务 */
@Service
@RequiredArgsConstructor
public class BizStoreAuthService {
    private final BizStoreAuthMapper mapper;

    public BizStoreAuth getById(Long id) { return mapper.selectBizStoreAuthById(id); }
    public List<BizStoreAuth> list(BizStoreAuth query) { return mapper.selectBizStoreAuthList(query); }
    public int add(BizStoreAuth entity) { return mapper.insertBizStoreAuth(entity); }
    public int update(BizStoreAuth entity) { return mapper.updateBizStoreAuth(entity); }
    public int delete(Long[] ids) { return mapper.deleteBizStoreAuthByIds(ids); }
}
