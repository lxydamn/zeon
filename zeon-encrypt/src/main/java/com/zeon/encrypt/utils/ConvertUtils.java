package com.zeon.encrypt.utils;

import java.lang.reflect.Array;
import java.util.List;

/**
 * <p>
 * </p>
 *
 * @author lxy2914344878@163.com
 * @serial 2025/8/20 20:15
 */
public class ConvertUtils {
    public static Object convertToTargetType(String value, Class<?> targetType) {
        if (targetType == String.class) {
            return value;
        } else if (targetType == Long.class || targetType == long.class) {
            return Long.valueOf(value);
        } else if (targetType == Integer.class || targetType == int.class) {
            return Integer.valueOf(value);
        } else if (targetType == Double.class || targetType == double.class) {
            return Double.valueOf(value);
        } else if (targetType == Float.class || targetType == float.class) {
            return Float.valueOf(value);
        } else if (targetType == Boolean.class || targetType == boolean.class) {
            return Boolean.valueOf(value);
        } else if (targetType == Byte.class || targetType == byte.class) {
            return Byte.valueOf(value);
        } else if (targetType == Short.class || targetType == short.class) {
            return Short.valueOf(value);
        } else if (targetType == Character.class || targetType == char.class && value.length() == 1) {
            return value.charAt(0);
        }
        // 其他类型可按需添加
        return value;
    }

    public static Object convertArrayToTargetType(String[] array, Class<?> targetType) {
        Class<?> componentType = targetType.getComponentType();
        Object targetArray = Array.newInstance(componentType, array.length);
        for (int i = 0; i < array.length; i++) {
            Object converted = convertToTargetType(array[i], componentType);
            Array.set(targetArray, i, converted);
        }
        return targetArray;
    }

    public static Object convertListToTargetArrayType(List<Object> list, Class<?> targetType) {
        Class<?> componentType = targetType.getComponentType();
        Object targetArray = Array.newInstance(componentType, list.size());
        for (int i = 0; i < list.size(); i++) {
            Object converted = convertToTargetType((String) list.get(i), componentType);
            Array.set(targetArray, i, converted);
        }
        return targetArray;
    }

    public static boolean isBasicType(Class<?> clazz) {
        return clazz.isPrimitive() || clazz.equals(String.class) || clazz.equals(Integer.class)
                        || clazz.equals(Long.class) || clazz.equals(Float.class) || clazz.equals(Double.class)
                        || clazz.equals(Boolean.class) || clazz.equals(Byte.class) || clazz.equals(Short.class)
                        || clazz.equals(Character.class);
    }
}
