package com.zeon.db.utils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * <p></p>
 *
 * @author xingyang.li@hand-china.com  2025/8/16 16:14
 */
public class SqlUtils {
	/**
	 *
	 * @param list entity's list
	 * @param fieldName entity field name
	 * @return "v1,v2,v3"
	 * @param <T> clazz
	 */
	public static <T> String getIds(List<T> list, String fieldName) {
		if (CollectionUtils.isEmpty(list)) {
			return null;
		}
		Class<?> clazz = list.get(0).getClass();
		Field field = ReflectUtils.getField(clazz, fieldName);
		if (field == null) {
			return null;
		}
		field.setAccessible(true);
		boolean isString = field.getType().equals(String.class);
		String result =  list.stream().map(item -> ReflectionUtils.getField(field, item))
				.filter(Objects::nonNull)
				.map(item -> String.format("'%s'", item))
				.distinct()
				.collect(Collectors.joining(","));

		if (!StringUtils.hasLength(result)) {
			return null;
		}
		return result;
	}

	/**
	 * 驼峰转下划线
	 * @param camelCaseStr 驼峰字符串
	 * @return 下划线字符串
	 */
	public static String camelToUnderline(String camelCaseStr) {
		if (camelCaseStr == null || camelCaseStr.isEmpty()) {
			return camelCaseStr;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < camelCaseStr.length(); i++) {
			char c = camelCaseStr.charAt(i);
			if (Character.isUpperCase(c)) {
				// 如果不是第一个字符，前面加下划线
				if (i > 0) {
					sb.append('_');
				}
				sb.append(Character.toLowerCase(c));
			} else {
				sb.append(c);
			}
		}
		return sb.toString();
	}
}
