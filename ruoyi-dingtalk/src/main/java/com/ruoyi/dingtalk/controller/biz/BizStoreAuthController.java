package com.ruoyi.dingtalk.controller.biz;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.dingtalk.domain.biz.BizStoreAuth;
import com.ruoyi.dingtalk.service.biz.BizStoreAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * 店铺授权接口。
 * 说明：先提供基础 CRUD，后续前端联动筛选、Excel导入都会复用这些接口。
 */
@RestController
@RequestMapping("/biz/storeAuth")
@RequiredArgsConstructor
public class BizStoreAuthController {
    private final BizStoreAuthService service;

    @GetMapping("/list")
    public AjaxResult list(BizStoreAuth query) { return AjaxResult.success(service.list(query)); }

    @GetMapping("/{id}")
    public AjaxResult get(@PathVariable Long id) { return AjaxResult.success(service.getById(id)); }

    @PostMapping
    public AjaxResult add(@RequestBody BizStoreAuth entity) { return AjaxResult.success(service.add(entity)); }

    @PutMapping
    public AjaxResult edit(@RequestBody BizStoreAuth entity) { return AjaxResult.success(service.update(entity)); }

    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) { return AjaxResult.success(service.delete(ids)); }
}
