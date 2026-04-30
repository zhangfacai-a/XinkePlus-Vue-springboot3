package com.ruoyi.dingtalk.controller.biz;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.dingtalk.domain.biz.BizCompanySubject;
import com.ruoyi.dingtalk.service.biz.BizCompanySubjectService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 公司主体接口。
 * 说明：先提供基础 CRUD，后续前端联动筛选、Excel导入都会复用这些接口。
 */
@RestController
@RequestMapping("/biz/companySubject")
@RequiredArgsConstructor
public class BizCompanySubjectController {
    private final BizCompanySubjectService service;

    @GetMapping("/list")
    public AjaxResult list(BizCompanySubject query) { return AjaxResult.success(service.list(query)); }

    @GetMapping("/{id}")
    public AjaxResult get(@PathVariable Long id) { return AjaxResult.success(service.getById(id)); }

    @PostMapping
    public AjaxResult add(@RequestBody BizCompanySubject entity) { return AjaxResult.success(service.add(entity)); }

    @PutMapping
    public AjaxResult edit(@RequestBody BizCompanySubject entity) { return AjaxResult.success(service.update(entity)); }

    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) { return AjaxResult.success(service.delete(ids)); }
}
