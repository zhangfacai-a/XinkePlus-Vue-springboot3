package com.ruoyi.dingtalk.controller.biz;

import com.ruoyi.common.core.domain.AjaxResult;
import com.ruoyi.dingtalk.domain.imports.BizImportBatch;
import com.ruoyi.dingtalk.mapper.BizImportBatchMapper;
import com.ruoyi.dingtalk.mapper.BizImportErrorLogMapper;
import com.ruoyi.dingtalk.service.imports.BizImportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * Excel模板导入接口。
 */
@RestController
@RequestMapping("/biz/import")
@RequiredArgsConstructor
public class BizImportController {
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

    /** 查询导入批次 */
    @GetMapping("/batch/list")
    public AjaxResult batchList(BizImportBatch query) {
        return AjaxResult.success(batchMapper.selectList(query));
    }

    /** 查询某次导入的错误明细 */
    @GetMapping("/batch/{batchId}/errors")
    public AjaxResult errorList(@PathVariable Long batchId) {
        return AjaxResult.success(errorLogMapper.selectByBatchId(batchId));
    }
}
