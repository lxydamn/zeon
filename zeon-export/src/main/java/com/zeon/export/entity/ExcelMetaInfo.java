package com.zeon.export.entity;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.util.StringUtils;

import com.zeon.export.annotations.Export;
import com.zeon.export.annotations.ExportColumn;
import com.zeon.export.annotations.ExportEntity;
import com.zeon.export.constants.FileType;

import lombok.Builder;
import lombok.Data;

/**
 * <p>
 * </p>
 *
 * @author lxy2914344878@163.com
 * @since 2025/9/3 11:21
 */
@Data
@Builder
public class ExcelMetaInfo {
    private String filename;
    private FileType fileType;
    private List<ExportColumn> exportColumns;
    private List<List<String>> header;
    private Export export;

    public static ExcelMetaInfo of(Export export) {
        Class<?> targetType = export.value();
        FileType fileType = export.type();
        ExportEntity entity = targetType.getDeclaredAnnotation(ExportEntity.class);
        String filename = StringUtils.hasLength(entity.name()) ? entity.name() : targetType.getSimpleName();
        Field[] fields = targetType.getDeclaredFields();
        List<List<String>> headers = new ArrayList<>();
        List<ExportColumn> exportColumns = new ArrayList<>();
        // Construct xlsx headers
        for (Field field : fields) {
            ExportColumn exportColumn = field.getDeclaredAnnotation(ExportColumn.class);
            if (exportColumn == null) {
                continue;
            }
            String[] name = exportColumn.name();
            headers.add(name.length == 0 ? Collections.singletonList(field.getName()) : Arrays.asList(name));
            exportColumns.add(exportColumn);
        }
        return ExcelMetaInfo.builder().filename(filename).fileType(fileType).header(headers)
                        .exportColumns(exportColumns).export(export).build();
    }
}
