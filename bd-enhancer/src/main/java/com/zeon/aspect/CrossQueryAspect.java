package com.zeon.aspect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.alibaba.fastjson2.JSONReader;
import com.zeon.annotations.CrossQuery;
import com.zeon.annotations.CrossQueryEntity;
import com.zeon.annotations.CrossQueryField;
import com.zeon.domain.BaseDomain;
import com.zeon.domain.KeyValueEntity;
import com.zeon.mapper.CommonDbEnhancedMapper;
import com.zeon.utils.ReflectUtils;
import jakarta.annotation.Resource;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

/**
 * <p></p>
 *
 * @author xingyang.li@hand-china.com  2025/8/16 16:46
 */
@Aspect
@Component
public class CrossQueryAspect {

	private static final Logger logger = LoggerFactory.getLogger(CrossQueryAspect.class);

	@Resource
	private CommonDbEnhancedMapper commonDbEnhancedMapper;

	@AfterReturning(value = "@annotation(crossQuery)", returning = "value")
	public void doAfterReturning(JoinPoint joinPoint, CrossQuery crossQuery, Object value) {
		logger.info("<=== start execute cross query, method {} ====> ", joinPoint.getSignature().getName());
		if (value == null) {
			logger.info("return value is null");
			return;
		}

		MethodSignature signature = (MethodSignature) joinPoint.getSignature();
		logger.info("Signature Return Type: {}", signature.getReturnType());

		List<Object> list = new ArrayList<>();
		if (value instanceof BaseDomain || value.getClass().isArray()) {
			list.add(value);
		} else if (value instanceof List<?>) {
			list.addAll((Collection<?>) value);
		}

		if (CollectionUtils.isEmpty(list)) {
			logger.info("return value is empty");
			return;
		}

		Class<?> returnClazz = list.get(0).getClass();
		Field[] declaredFields = returnClazz.getDeclaredFields();

		for (Field declaredField : declaredFields) {
			declaredField.setAccessible(true);
			CrossQueryField crossQueryField = declaredField.getDeclaredAnnotation(CrossQueryField.class);
			CrossQueryEntity crossQueryEntity = declaredField.getDeclaredAnnotation(CrossQueryEntity.class);
			if (crossQueryField != null) {
				processCrossQuery(crossQueryField, returnClazz, list);
			}
			if (crossQueryEntity != null) {
				processCrossQuery(crossQueryEntity, returnClazz, list);
			}
		}
	}

	private void processCrossQuery(CrossQueryEntity queryEntity, Class<?> returnClazz, List<Object> values) {
		String keyFieldName = queryEntity.keyField();
		String entityFieldName = queryEntity.valueField();
		String tableName = queryEntity.tableName();

		Field keyField = ReflectUtils.getField(returnClazz, keyFieldName);
		Field entityField = ReflectUtils.getField(returnClazz, entityFieldName);
		keyField.setAccessible(true);
		entityField.setAccessible(true);

		Class<?> entityFieldType = entityField.getType();

		Map<Object, JSONObject> map = commonDbEnhancedMapper.selectMap(values, tableName, keyFieldName);
		for (Object value : values) {
			Object key = ReflectionUtils.getField(keyField, value);
			JSONObject jsonObject = map.get(key);
			Object targetEntity = JSON.parseObject(jsonObject.toJSONString(), entityFieldType, JSONReader.Feature.SupportSmartMatch);
			ReflectionUtils.setField(entityField, value, targetEntity);
		}

	}

	private void processCrossQuery(CrossQueryField queryField, Class<?> clazz, List<Object> values) {

		String keyFieldName = queryField.keyField();
		String valueFieldName = queryField.valueField();
		String tableName = queryField.tableName();

		if (StringUtils.hasLength(queryField.tableKeyField()))
			keyFieldName = queryField.tableKeyField();
		if (StringUtils.hasLength(queryField.tableValueField()))
			valueFieldName = queryField.tableValueField();

		Field keyField = ReflectUtils.getField(clazz, queryField.keyField());
		Field valueField = ReflectUtils.getField(clazz, queryField.valueField());
		keyField.setAccessible(true);
		valueField.setAccessible(true);

		Map<Object, JSONObject> map = commonDbEnhancedMapper.selectListUseKeyValue(values, tableName, keyFieldName, valueFieldName);

		for (Object object: values) {
			Object key = ReflectionUtils.getField(keyField, object);
			JSONObject jsonObject = map.get(key);
			if (jsonObject != null) {
				ReflectionUtils.setField(valueField, object, jsonObject.get("value"));
			}
		}
	}

	private boolean isBaseType(Object value) {
		return value.getClass().getClassLoader() == null;
	}
}
