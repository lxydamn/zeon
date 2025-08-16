package com.zeon.mapper.provider;

import java.util.List;

import com.zeon.utils.SqlUtils;

/**
 * <p></p>
 *
 * @author xingyang.li@hand-china.com  2025/8/16 16:12
 */
public class CommonProvider {

	public static <T> String selectListUseKeyValue(List<T> list, String tableName, String keyField, String valueField) {
		StringBuilder sql = new StringBuilder();
		// Generate keys
		String ids = SqlUtils.getIds(list, keyField);
		String tableKey = SqlUtils.camelToUnderline(keyField);
		String tableValue = SqlUtils.camelToUnderline(valueField);
		sql.append("SELECT ")
				.append(tableKey)
				.append(" AS _xxy")
				.append(",")
				.append(tableValue)
				.append(" AS value")
				.append(" FROM ")
				.append(tableName)
				.append(" WHERE ")
				.append(keyField)
				.append(" IN (")
				.append(ids)
				.append(")");

		return sql.toString();
	}

	public static <T> String selectMap(List<T> list, String tableName, String keyField) {
		StringBuilder sql = new StringBuilder();

		String ids = SqlUtils.getIds(list, keyField);
		String tableKey = SqlUtils.camelToUnderline(keyField);

		sql.append("SELECT * ,")
				.append(tableKey)
				.append(" AS _xxy")
				.append(" FROM ")
				.append(tableName)
				.append(" WHERE ")
				.append(tableKey)
				.append(" IN (")
				.append(ids)
				.append(")");

		return sql.toString();
	}
}
