package com.ruoyi.dingtalk.service.imports;

import com.ruoyi.dingtalk.domain.imports.BizImportTemplate;
import lombok.Data;
import org.apache.poi.ss.usermodel.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Excel读取服务：只负责把 Excel 按表头解析成行数据，不处理业务入库。
 */
@Service
public class ExcelReadService {

    public ParseResult parse(MultipartFile file, BizImportTemplate template) {
        try (InputStream in = file.getInputStream(); Workbook workbook = WorkbookFactory.create(in)) {
            Sheet sheet = resolveSheet(workbook, template.getSheetName());
            int headerRowIndex = Math.max(nullToOne(template.getHeaderRowNo()) - 1, 0);
            int dataStartIndex = Math.max(nullToOne(template.getDataStartRowNo()) - 1, headerRowIndex + 1);

            Row headerRow = sheet.getRow(headerRowIndex);
            if (headerRow == null) {
                throw new IllegalArgumentException("未找到表头行，headerRowNo=" + template.getHeaderRowNo());
            }

            DataFormatter formatter = new DataFormatter(Locale.CHINA);
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            Map<Integer, String> headers = new LinkedHashMap<>();
            for (Cell cell : headerRow) {
                String name = cellToString(cell, formatter, evaluator);
                if (name != null && !name.isBlank()) {
                    headers.put(cell.getColumnIndex(), name.trim());
                }
            }

            List<ExcelRow> rows = new ArrayList<>();
            int lastRowNum = sheet.getLastRowNum();
            for (int i = dataStartIndex; i <= lastRowNum; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue;
                }
                Map<String, String> values = new LinkedHashMap<>();
                boolean allBlank = true;
                for (Map.Entry<Integer, String> entry : headers.entrySet()) {
                    Cell cell = row.getCell(entry.getKey());
                    String value = cellToString(cell, formatter, evaluator);
                    if (value != null && !value.isBlank()) {
                        allBlank = false;
                    }
                    values.put(entry.getValue(), value == null ? null : value.trim());
                }
                if (!allBlank) {
                    ExcelRow excelRow = new ExcelRow();
                    excelRow.setSheetName(sheet.getSheetName());
                    excelRow.setRowNo(i + 1);
                    excelRow.setValues(values);
                    rows.add(excelRow);
                }
            }
            ParseResult result = new ParseResult();
            result.setSheetName(sheet.getSheetName());
            result.setHeaders(new ArrayList<>(headers.values()));
            result.setRows(rows);
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Excel解析失败：" + e.getMessage(), e);
        }
    }

    private Sheet resolveSheet(Workbook workbook, String sheetName) {
        if (sheetName != null && !sheetName.isBlank()) {
            Sheet sheet = workbook.getSheet(sheetName);
            if (sheet != null) {
                return sheet;
            }
        }
        return workbook.getSheetAt(0);
    }

    private int nullToOne(Integer value) {
        return value == null ? 1 : value;
    }

    private String cellToString(Cell cell, DataFormatter formatter, FormulaEvaluator evaluator) {
        if (cell == null) return null;
        try {
            if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
                return new SimpleDateFormat("yyyy-MM-dd").format(cell.getDateCellValue());
            }
            return formatter.formatCellValue(cell, evaluator);
        } catch (Exception e) {
            return null;
        }
    }

    @Data
    public static class ParseResult {
        private String sheetName;
        private List<String> headers;
        private List<ExcelRow> rows;
    }

    @Data
    public static class ExcelRow {
        private String sheetName;
        private Integer rowNo;
        /** key 是 Excel 列名，value 是单元格文本 */
        private Map<String, String> values;
    }
}
