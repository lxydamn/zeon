package com.zeon.export.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.util.ReflectionUtils;

import com.zeon.export.annotations.ExportColumn;
import com.zeon.export.entity.ExcelMetaInfo;

import jakarta.servlet.http.HttpServletResponse;

/**
 * <p>
 * </p>
 *
 * @author lxy2914344878@163.com
 * @since 2025/9/3 19:57
 */
public class ResponseUtils {

    public static void constructFileResponse(HttpServletResponse response, String filename) {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        response.setHeader("Content-disposition", "attachment;filename=" + filename);
    }

    public static List<List<Object>> extractData(Collection<?> data, ExcelMetaInfo metaInfo) {
        Class<?> clazz = metaInfo.getExport().value();
        Field[] fields = clazz.getDeclaredFields();
        // Set all fields accessible
        Arrays.stream(fields).forEach(field -> field.setAccessible(true));
        // Get all fields with ExportColumn annotation
        List<Field> targetFields = Arrays.stream(fields)
                        .filter(field -> field.getDeclaredAnnotation(ExportColumn.class) != null).toList();
        // Extract data
        List<List<Object>> results = new ArrayList<>(data.size());
        for (Object obj : data) {
            List<Object> objects = new ArrayList<>(targetFields.size());
            for (Field field : targetFields) {
                Object value = ReflectionUtils.getField(field, obj);
                objects.add(value);
            }
            results.add(objects);
        }
        return results;
    }
}
