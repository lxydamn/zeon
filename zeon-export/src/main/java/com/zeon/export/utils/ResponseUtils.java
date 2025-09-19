package com.zeon.export.utils;

import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.springframework.http.HttpHeaders;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.zeon.export.annotations.ExportColumn;
import com.zeon.export.constants.FileType;
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

    public static HttpServletResponse getHttpServletResponse() {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            throw new RuntimeException("ServletRequestAttributes is null");
        }
        HttpServletResponse response = attributes.getResponse();
        if (response == null) {
            throw new RuntimeException("HttpServletResponse is null");
        }
        return response;
    }

    public static String getEncodedFilename(String filename, FileType fileType) {
        try {
            return URLEncoder.encode(filename, StandardCharsets.UTF_8).replaceAll("\\+", "%20") + "."
                            + fileType.getValue();
        } catch (Exception e) {
            // This should never happen with StandardCharsets.UTF_8
            LocalDateTime now = LocalDateTime.now(ZoneId.of("Asia/Shanghai"));
            return now.format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "." + fileType.getValue();
        }
    }

    public static void constructFileResponse(HttpServletResponse response, ExcelMetaInfo excelMetaInfo) {
        response.setContentType("application/vnd.ms-excel");
        response.setCharacterEncoding("utf-8");
        String encodedFilename = getEncodedFilename(excelMetaInfo.getFilename(), excelMetaInfo.getFileType());
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=" + encodedFilename + "; filename*=UTF-8''" + encodedFilename);
    }

    public static List<List<String>> extractData(JSONArray json, ExcelMetaInfo metaInfo) {
        List<String> fields = metaInfo.getFieldName();
        List<List<String>> data = new ArrayList<>();
        List<JSONObject> javaList = json.toJavaList(JSONObject.class);
        for (JSONObject jsonObject : javaList) {
            List<String> row = new ArrayList<>();
            for (String field : fields) {
                row.add(jsonObject.getString(field));
            }
            data.add(row);
        }
        return data;
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
