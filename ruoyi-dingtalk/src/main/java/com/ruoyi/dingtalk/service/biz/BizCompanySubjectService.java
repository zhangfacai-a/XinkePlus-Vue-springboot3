package com.ruoyi.dingtalk.service.biz;

import com.ruoyi.dingtalk.domain.biz.BizCompanySubject;
import com.ruoyi.dingtalk.mapper.BizCompanySubjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

/** CompanySubject 业务服务 */
@Service
@RequiredArgsConstructor
public class BizCompanySubjectService {
    private final BizCompanySubjectMapper mapper;

    public BizCompanySubject getById(Long id) { return mapper.selectBizCompanySubjectById(id); }
    public List<BizCompanySubject> list(BizCompanySubject query) { return mapper.selectBizCompanySubjectList(query); }
    public int add(BizCompanySubject entity) { return mapper.insertBizCompanySubject(entity); }
    public int update(BizCompanySubject entity) { return mapper.updateBizCompanySubject(entity); }
    public int delete(Long[] ids) { return mapper.deleteBizCompanySubjectByIds(ids); }
}
