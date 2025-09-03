package com.zeon.db.utils;

import java.lang.reflect.Field;

import org.springframework.util.ReflectionUtils;

/**
 * <p></p>
 *
 * @author xingyang.li@hand-china.com  2025/8/16 16:23
 */
public class ReflectUtils {

	public static Field getField(Class<?> clazz, String fieldName) {
		try {
			Field field = null;
			while (field == null) {
				field = clazz.getDeclaredField(fieldName);
				clazz = clazz.getSuperclass();
			}
			return field;
		} catch (NoSuchFieldException e) {
			throw new RuntimeException(e);
		}
	}
}
