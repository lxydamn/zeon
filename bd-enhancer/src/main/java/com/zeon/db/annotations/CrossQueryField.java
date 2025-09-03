package com.zeon.db.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 *     仅关联字段查询
 * </p>
 *
 * @author xingyang.li@hand-china.com  2025/8/16 16:06
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CrossQueryField {
	/**
	 * Key 字段
	 */
	String keyField();

	/**
	 * key Table 字段
	 */
	String tableKeyField() default "";

	/**
	 * Value 字段
	 */
	String valueField();

	/**
	 * Value Table 字段
	 */
	String tableValueField() default "";

	/**
	 * tableName
	 */
	String tableName();
}
