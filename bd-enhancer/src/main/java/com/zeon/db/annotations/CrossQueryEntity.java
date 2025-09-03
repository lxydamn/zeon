package com.zeon.db.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p></p>
 *
 * @author xingyang.li@hand-china.com  2025/8/16 18:55
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CrossQueryEntity {
	/**
	 * key字段名，默认取当前字段名
	 */
	String keyField();

	/**
	 * 表名
	 */
	String tableName();

	/**
	 * value字段名，查询目标
	 */
	String valueField();
}
