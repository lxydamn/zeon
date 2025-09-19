package com.zeon.export.constants;

import lombok.Getter;

/**
 * <p>
 * 文件类型枚举
 * </p>
 *
 * @author lxy2914344878@163.com
 * @since 2025/9/3 10:45
 */
@Getter
public enum FileType {
    XLSX("xlsx"), CSV("csv");

    private final String value;

    FileType(String value) {
        this.value = value;
    }

}
