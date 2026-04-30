package com.ruoyi.dingtalk.service.imports;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

/**
 * 导入字段转换工具。
 *
 * 注意：Excel 单元格进来时可能是：
 * 1）yyyy-MM-dd / yyyy/M/d / yyyy.M.d / yyyyMMdd
 * 2）Excel 日期序列号，例如 45658
 * 这里统一转成 java.util.Date，避免同一天因为不同格式导致唯一键匹配失败。
 */
public class ImportValueUtils {
    private ImportValueUtils() {}

    public static String str(Map<String, Object> row, String field) {
        Object v = row.get(field);
        if (v == null) return null;
        String s = String.valueOf(v).trim();
        if (s.isEmpty()) return null;
        if ("null".equalsIgnoreCase(s)) return null;
        return s;
    }

    public static String firstStr(Map<String, Object> row, String... fields) {
        if (fields == null) return null;
        for (String field : fields) {
            String v = str(row, field);
            if (v != null) return v;
        }
        return null;
    }

    public static Integer integer(Map<String, Object> row, String field) {
        String s = str(row, field);
        if (s == null) return null;
        return new BigDecimal(cleanNumber(s)).intValue();
    }

    public static Long lng(Map<String, Object> row, String field) {
        String s = str(row, field);
        if (s == null) return null;
        return new BigDecimal(cleanNumber(s)).longValue();
    }

    public static BigDecimal decimal(Map<String, Object> row, String field) {
        String s = str(row, field);
        if (s == null) return null;
        return new BigDecimal(cleanNumber(s).replace("%", ""));
    }

    public static Date date(Map<String, Object> row, String field) {
        String s = str(row, field);
        if (s == null) return null;
        return parseDate(s, field);
    }

    public static Date firstDate(Map<String, Object> row, String... fields) {
        if (fields == null) return null;
        for (String field : fields) {
            String s = str(row, field);
            if (s != null) return parseDate(s, field);
        }
        return null;
    }

    public static String required(Map<String, Object> row, String field, String label) {
        String s = str(row, field);
        if (s == null) throw new IllegalArgumentException(label + "不能为空");
        return s;
    }

    public static String requiredFirst(Map<String, Object> row, String label, String... fields) {
        String s = firstStr(row, fields);
        if (s == null) throw new IllegalArgumentException(label + "不能为空");
        return s;
    }

    public static Date requiredFirstDate(Map<String, Object> row, String label, String... fields) {
        Date d = firstDate(row, fields);
        if (d == null) throw new IllegalArgumentException(label + "不能为空");
        return d;
    }

    private static String cleanNumber(String s) {
        return s.replace(",", "").trim();
    }

    private static Date parseDate(String raw, String field) {
        String s = raw == null ? null : raw.trim();
        if (s == null || s.isEmpty()) return null;

        // Excel 日期序列号。Excel 以 1899-12-30 作为日期序列换算基准。
        if (s.matches("^\\d+(\\.0+)?$")) {
            long serial = new BigDecimal(s).longValue();
            long millis = (serial - 25569L) * 24L * 60L * 60L * 1000L;
            return new Date(millis);
        }

        String[] patterns = new String[] {
                "yyyy-MM-dd",
                "yyyy/M/d",
                "yyyy.MM.dd",
                "yyyy.M.d",
                "yyyyMMdd"
        };
        for (String pattern : patterns) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                sdf.setLenient(false);
                return sdf.parse(s);
            } catch (ParseException ignored) {
            }
        }
        throw new IllegalArgumentException(field + " 日期格式错误，期望 yyyy-MM-dd / yyyy/M/d / yyyy.M.d / yyyyMMdd / Excel日期序列号，实际：" + s);
    }
}
