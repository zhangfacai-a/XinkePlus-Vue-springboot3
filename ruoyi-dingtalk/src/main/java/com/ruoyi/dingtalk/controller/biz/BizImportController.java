package com.ruoyi.dingtalk.controller.biz;

import com.ruoyi.common.core.controller.BaseController;
import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.common.core.page.TableDataInfo;
import com.ruoyi.dingtalk.domain.imports.BizImportBatch;
import com.ruoyi.dingtalk.domain.imports.BizImportErrorLog;
import com.ruoyi.dingtalk.mapper.BizImportBatchMapper;
import com.ruoyi.dingtalk.mapper.BizImportErrorLogMapper;
import com.ruoyi.dingtalk.service.imports.BizImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Excel 模板导入接口。
 */
@RestController
@RequestMapping("/biz/import")
@RequiredArgsConstructor
public class BizImportController extends BaseController {

    private final BizImportService importService;
    private final BizImportBatchMapper batchMapper;
    private final BizImportErrorLogMapper errorLogMapper;

    /**
     * 按模板编码导入 Excel。
     * 示例：POST /biz/import/PROFIT_UNIT，form-data: file=xxx.xlsx
     */
    @PostMapping("/{templateCode}")
    public AjaxResult importExcel(@PathVariable String templateCode, @RequestParam("file") MultipartFile file) {
        return AjaxResult.success(importService.importExcel(templateCode, file));
    }

    /**
     * 查询导入批次列表。
     */
    @GetMapping("/batch/list")
    public TableDataInfo batchList(BizImportBatch query) {
        startPage();
        List<BizImportBatch> list = batchMapper.selectList(query);
        return getDataTable(list);
    }

    /**
     * 查询导入批次详情。
     * 设计接口：GET /biz/import/batch/{batchId}
     */
    @GetMapping("/batch/{batchId}")
    public AjaxResult batchInfo(@PathVariable Long batchId) {
        return AjaxResult.success(batchMapper.selectById(batchId));
    }

    /**
     * 查询某次导入的错误明细。
     * 设计接口：GET /biz/import/error/list?batchId=30
     */
    @GetMapping("/error/list")
    public TableDataInfo errorList(BizImportErrorLog query) {
        startPage();
        List<BizImportErrorLog> list = errorLogMapper.selectList(query);
        return getDataTable(list);
    }

    /**
     * 兼容旧接口：GET /biz/import/batch/{batchId}/errors
     */
    @GetMapping("/batch/{batchId}/errors")
    public AjaxResult errorListByBatchId(@PathVariable Long batchId) {
        return AjaxResult.success(errorLogMapper.selectByBatchId(batchId));
    }
}
