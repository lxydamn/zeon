package com.zeon.export.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * </p>
 *
 * @author lxy2914344878@163.com
 * @since 2025/9/3 10:14
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface ExportColumn {
    /**
     * 列名
     */
    String[] name() default {};
}
