package com.ruoyi.dingtalk.service.imports;

import com.ruoyi.dingtalk.domain.imports.*;
import com.ruoyi.dingtalk.mapper.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Excel导入主流程。
 * 原则：先创建批次，再解析校验，逐行调用 Handler 入库，最后更新批次结果。
 */
@Service
@RequiredArgsConstructor
public class BizImportService {
    private final BizImportTemplateMapper templateMapper;
    private final BizImportTemplateColumnMapper columnMapper;
    private final BizImportBatchMapper batchMapper;
    private final BizImportErrorLogMapper errorLogMapper;
    private final ExcelReadService excelReadService;
    private final List<ImportHandler> handlers;

    @Transactional(rollbackFor = Exception.class)
    public BizImportResult importExcel(String templateCode, MultipartFile file) {
        BizImportTemplate template = templateMapper.selectByCode(templateCode);
        if (template == null || "1".equals(template.getStatus())) {
            throw new IllegalArgumentException("导入模板不存在或已停用：" + templateCode);
        }
        List<BizImportTemplateColumn> columns = columnMapper.selectByTemplateCode(templateCode);
        if (columns == null || columns.isEmpty()) {
            throw new IllegalArgumentException("模板字段映射未配置：" + templateCode);
        }
        ImportHandler handler = handlers.stream()
                .filter(h -> h.supportTemplateCode().equals(templateCode))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("未找到导入处理器：" + templateCode));

        BizImportBatch batch = new BizImportBatch();
        batch.setTemplateCode(templateCode);
        batch.setTemplateName(template.getTemplateName());
        batch.setFileName(file.getOriginalFilename());
        batch.setImportStatus("0");
        batch.setStartTime(new Date());
        batch.setCreateBy("excel_import");
        batchMapper.insertBatch(batch);

        int success = 0, fail = 0, skip = 0;
        String finalMsg = null;
        try {
            ExcelReadService.ParseResult parsed = excelReadService.parse(file, template);
            Map<String, BizImportTemplateColumn> byExcelColumn = columns.stream()
                    .collect(Collectors.toMap(BizImportTemplateColumn::getExcelColumn, Function.identity(), (a, b) -> a, LinkedHashMap::new));

            List<String> missingColumns = validateRequiredColumns(parsed.getHeaders(), columns);
            if (!missingColumns.isEmpty()) {
                for (String missing : missingColumns) {
                    writeError(batch.getId(), parsed.getSheetName(), null, missing, null, null, "COLUMN_MISSING", "缺少必填列：" + missing);
                }
                throw new IllegalArgumentException("Excel缺少必填列：" + String.join(",", missingColumns));
            }

            for (ExcelReadService.ExcelRow excelRow : parsed.getRows()) {
                try {
                    Map<String, Object> row = convertToDbFieldRow(excelRow.getValues(), byExcelColumn);
                    if (row.isEmpty()) { skip++; continue; }
                    validateRequiredValues(row, excelRow, columns, batch.getId());
                    handler.importRow(row, excelRow.getRowNo(), batch.getId());
                    success++;
                } catch (Exception ex) {
                    fail++;
                    writeError(batch.getId(), excelRow.getSheetName(), excelRow.getRowNo(), null, null, null, "ROW_ERROR", ex.getMessage());
                }
            }
            finalMsg = buildStatusMessage(success, fail, skip);
        } catch (Exception e) {
            finalMsg = e.getMessage();
            if (success == 0 && fail == 0) {
                fail = 1;
            }
        }

        batch.setTotalCount(success + fail + skip);
        batch.setSuccessCount(success);
        batch.setFailCount(fail);
        batch.setSkipCount(skip);
        batch.setEndTime(new Date());
        batch.setErrorMsg(finalMsg);
        batch.setImportStatus(resolveStatus(success, fail));
        batchMapper.updateBatch(batch);

        BizImportResult result = new BizImportResult();
        result.setBatchId(batch.getId());
        result.setTotalCount(batch.getTotalCount());
        result.setSuccessCount(success);
        result.setFailCount(fail);
        result.setSkipCount(skip);
        result.setImportStatus(batch.getImportStatus());
        result.setMessage(finalMsg);
        return result;
    }

    private List<String> validateRequiredColumns(List<String> headers, List<BizImportTemplateColumn> columns) {
        Set<String> headerSet = new HashSet<>(headers);
        List<String> missing = new ArrayList<>();
        for (BizImportTemplateColumn column : columns) {
            if ("1".equals(column.getRequiredFlag()) && !headerSet.contains(column.getExcelColumn())) {
                missing.add(column.getExcelColumn());
            }
        }
        return missing;
    }

    private Map<String, Object> convertToDbFieldRow(Map<String, String> excelValues, Map<String, BizImportTemplateColumn> mapping) {
        Map<String, Object> row = new LinkedHashMap<>();
        for (Map.Entry<String, String> entry : excelValues.entrySet()) {
            BizImportTemplateColumn column = mapping.get(entry.getKey());
            if (column == null) continue;

            String dbField = column.getDbField();
            String value = entry.getValue();
            if ((value == null || value.isBlank()) && column.getDefaultValue() != null) {
                value = column.getDefaultValue();
            }

            /*
             * 防止多个 Excel 表头错误/历史原因映射到同一个 dbField 时，后面的空单元格覆盖前面的有效值。
             * 例如："结束日期" 和 "停运日期" 如果都配成 stop_date，AE列空值会把M列结束日期覆盖成空，
             * 最终出现“结束日期不能为空”。
             */
            Object oldValue = row.get(dbField);
            boolean oldBlank = oldValue == null || String.valueOf(oldValue).isBlank();
            boolean newBlank = value == null || value.isBlank();
            if (!row.containsKey(dbField) || oldBlank || !newBlank) {
                if (!(oldValue != null && !String.valueOf(oldValue).isBlank() && newBlank)) {
                    row.put(dbField, value);
                }
            }
        }
        return row;
    }

    private void validateRequiredValues(Map<String, Object> row, ExcelReadService.ExcelRow excelRow, List<BizImportTemplateColumn> columns, Long batchId) {
        for (BizImportTemplateColumn column : columns) {
            if (!"1".equals(column.getRequiredFlag())) continue;
            Object value = row.get(column.getDbField());
            if (value == null || String.valueOf(value).isBlank()) {
                writeError(batchId, excelRow.getSheetName(), excelRow.getRowNo(), column.getExcelColumn(), column.getDbField(), null, "REQUIRED", column.getExcelColumn() + "不能为空");
                throw new IllegalArgumentException("第" + excelRow.getRowNo() + "行【" + column.getExcelColumn() + "】不能为空");
            }
        }
    }

    private void writeError(Long batchId, String sheetName, Integer rowNo, String columnName, String fieldName, String rawValue, String errorType, String errorMsg) {
        BizImportErrorLog log = new BizImportErrorLog();
        log.setBatchId(batchId);
        log.setSheetName(sheetName);
        log.setRowNo(rowNo);
        log.setColumnName(columnName);
        log.setFieldName(fieldName);
        log.setRawValue(rawValue);
        log.setErrorType(errorType);
        log.setErrorMsg(errorMsg);
        errorLogMapper.insertError(log);
    }

    private String resolveStatus(int success, int fail) {
        if (fail == 0 && success > 0) return "1";
        if (success > 0) return "2";
        return "3";
    }

    private String buildStatusMessage(int success, int fail, int skip) {
        return "导入完成：成功" + success + "条，失败" + fail + "条，跳过" + skip + "条";
    }
}
