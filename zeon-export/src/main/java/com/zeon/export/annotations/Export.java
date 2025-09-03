package com.zeon.export.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.zeon.export.constants.ExportMethod;
import com.zeon.export.constants.FileType;

/**
 * <p>
 * </p>
 *
 * @author lxy2914344878@163.com
 * @since 2025/9/3 10:09
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Export {
    /**
     * 导出类
     */
    Class<?> value();

    /**
     * 导出方式
     */
    ExportMethod method() default ExportMethod.SYNC;

    /**
     * 导出文件类型
     */
    FileType type() default FileType.XLSX;
}
