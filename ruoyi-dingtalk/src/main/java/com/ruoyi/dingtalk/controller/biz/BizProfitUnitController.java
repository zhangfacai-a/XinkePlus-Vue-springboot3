package com.ruoyi.dingtalk.controller.biz;

import com.ruoyi.common.annotation.Log;
import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.common.enums.BusinessType;
import com.ruoyi.common.utils.poi.ExcelUtil;
import com.ruoyi.dingtalk.domain.biz.BizProfitUnit;
import com.ruoyi.dingtalk.service.biz.BizProfitUnitService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * 利润单元档案接口。
 */
@RestController
@RequestMapping("/biz/profitUnit")
@RequiredArgsConstructor
public class BizProfitUnitController extends BaseController {

    private final BizProfitUnitService service;

    /**
     * 查询利润单元列表。
     */
    @PreAuthorize("@ss.hasPermi('biz:profitUnit:list')")
    @GetMapping("/list")
    public TableDataInfo list(BizProfitUnit query) {
        startPage();
        List<BizProfitUnit> list = service.list(query);
        return getDataTable(list);
    }

    /**
     * 导出利润单元。
     */
    @PreAuthorize("@ss.hasPermi('biz:profitUnit:export')")
    @Log(title = "利润单元档案", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, BizProfitUnit query) {
        List<BizProfitUnit> list = service.list(query);
        ExcelUtil<BizProfitUnit> util = new ExcelUtil<>(BizProfitUnit.class);
        util.exportExcel(response, list, "利润单元档案数据");
    }

    /**
     * 查询利润单元详情。
     */
    @PreAuthorize("@ss.hasPermi('biz:profitUnit:query')")
    @GetMapping("/{id}")
    public AjaxResult getInfo(@PathVariable Long id) {
        return AjaxResult.success(service.getById(id));
    }

    /**
     * 新增利润单元。
     */
    @PreAuthorize("@ss.hasPermi('biz:profitUnit:add')")
    @Log(title = "利润单元档案", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody BizProfitUnit entity) {
        return toAjax(service.add(entity));
    }

    /**
     * 修改利润单元。
     */
    @PreAuthorize("@ss.hasPermi('biz:profitUnit:edit')")
    @Log(title = "利润单元档案", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody BizProfitUnit entity) {
        return toAjax(service.update(entity));
    }

    /**
     * 删除利润单元。
     */
    @PreAuthorize("@ss.hasPermi('biz:profitUnit:remove')")
    @Log(title = "利润单元档案", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Long[] ids) {
        return toAjax(service.delete(ids));
    }
}
