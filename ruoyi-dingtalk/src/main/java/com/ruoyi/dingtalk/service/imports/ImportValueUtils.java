package com.ruoyi.dingtalk.service.imports;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/** 导入字段转换工具 */
public class ImportValueUtils {
    private ImportValueUtils() {}

    public static String str(Map<String, Object> row, String field) {
        Object v = row.get(field);
        if (v == null) return null;
        String s = String.valueOf(v).trim();
        return s.isEmpty() ? null : s;
    }

    public static Integer integer(Map<String, Object> row, String field) {
        String s = str(row, field);
        if (s == null) return null;
        return new BigDecimal(s.replace(",", "")).intValue();
    }

    public static Long lng(Map<String, Object> row, String field) {
        String s = str(row, field);
        if (s == null) return null;
        return new BigDecimal(s.replace(",", "")).longValue();
    }

    public static BigDecimal decimal(Map<String, Object> row, String field) {
        String s = str(row, field);
        if (s == null) return null;
        s = s.replace(",", "").replace("%", "");
        return new BigDecimal(s);
    }

    public static Date date(Map<String, Object> row, String field) {
        String s = str(row, field);
        if (s == null) return null;
        try { return new SimpleDateFormat("yyyy-MM-dd").parse(s); } catch (Exception ignored) {}
        try { return new SimpleDateFormat("yyyy/M/d").parse(s); } catch (Exception ignored) {}
        throw new IllegalArgumentException(field + " 日期格式错误，期望 yyyy-MM-dd，实际：" + s);
    }

    public static String required(Map<String, Object> row, String field, String label) {
        String s = str(row, field);
        if (s == null) throw new IllegalArgumentException(label + "不能为空");
        return s;
    }
}
