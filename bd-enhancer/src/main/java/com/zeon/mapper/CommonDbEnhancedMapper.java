package com.zeon.mapper;

import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSONObject;
import com.zeon.domain.KeyValueEntity;
import com.zeon.mapper.provider.CommonProvider;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.SelectProvider;

/**
 * <p></p>
 *
 * @author xingyang.li@hand-china.com  2025/8/16 15:52
 */
@Mapper
public interface CommonDbEnhancedMapper {
	/**
	 * 通用K,V型查询
	 * @param tableName tableName
	 * @param keyField keyFieldName
	 * @param valueField valueFieldValue
	 * @param list 实体列表
	 * @return list
	 * @param <T> Entity
	 */
	@SelectProvider(value = CommonProvider.class, method = "selectListUseKeyValue")
	@MapKey("_xxy")
	<T> Map<Object, JSONObject> selectListUseKeyValue(List<T> list, String tableName, String keyField, String valueField);

	/**
	 * 通用K,V型查询
	 * @param tableName tableName
	 * @param keyField keyFieldName
	 * @param list 实体列表
	 * @return list
	 * @param <T> Entity
	 */
	@SelectProvider(value = CommonProvider.class, method = "selectMap")
	@MapKey("_xxy")
	<T> Map<Object, JSONObject> selectMap(List<T> list, String tableName, String keyField);
}
