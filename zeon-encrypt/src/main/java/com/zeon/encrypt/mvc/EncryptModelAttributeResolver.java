package com.zeon.encrypt.mvc;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.BeanUtils;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import com.zeon.encrypt.core.Encrypt;
import com.zeon.encrypt.utils.ConvertUtils;
import com.zeon.encrypt.utils.EncryptUtils;

/**
 * <p>
 * </p>
 *
 * @author lxy2914344878@163.com
 * @serial 2025/8/20 21:22
 */
public class EncryptModelAttributeResolver implements HandlerMethodArgumentResolver {

    private final boolean annotationNotRequired;

    public EncryptModelAttributeResolver(boolean annotationNotRequired) {
        this.annotationNotRequired = annotationNotRequired;
    }

    public boolean supportsParameter(MethodParameter parameter) {
        return (parameter.hasParameterAnnotation(ModelAttribute.class)
                        || this.annotationNotRequired && !BeanUtils.isSimpleProperty(parameter.getParameterType()))
                        && parameter.hasMethodAnnotation(Encrypt.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                    NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        Class<?> paramType = parameter.getParameterType();
        Encrypt encrypt = parameter.getParameterAnnotation(Encrypt.class);
        // 1. 如果是 String 或基本类型，直接从请求参数取值并解密
        if (String.class.isAssignableFrom(paramType)) {
            String value = webRequest.getParameter(parameter.getParameterName());
            if (value != null) {
                return EncryptUtils.decrypt(value, encrypt);
            }
            return null;
        }

        // 2. 如果是数组或集合
        if (paramType.isArray()) {
            String[] values = webRequest.getParameterValues(parameter.getParameterName());
            if (values != null) {
                for (int i = 0; i < values.length; i++) {
                    values[i] = EncryptUtils.decrypt(values[i], encrypt);
                }
            }
            return values;
        }

        if (List.class.isAssignableFrom(paramType)) {
            String[] values = webRequest.getParameterValues(parameter.getParameterName());
            List<String> list = new ArrayList<>();
            if (values != null) {
                for (String val : values) {
                    list.add(EncryptUtils.decrypt(val, encrypt));
                }
            }
            return list;
        }

        // 3. POJO 对象：先实例化，再绑定参数并解密字段
        Object attribute = paramType.getDeclaredConstructor().newInstance();
        Field[] fields = paramType.getDeclaredFields();
        for (Field field : fields) {
            Encrypt fieldEncrypt = field.getAnnotation(Encrypt.class);
            field.setAccessible(true);
            String fieldName = field.getName();
            Class<?> fieldType = field.getType();
            String[] paramValues = webRequest.getParameterValues(fieldName);
            if (paramValues == null || paramValues.length == 0) {
                continue;
            }
            if (fieldEncrypt == null) {
                basicProcess(field, attribute, fieldType, paramValues);
            } else {
                decryptProcess(field, attribute, fieldEncrypt, fieldType, paramValues);
            }
        }
        return attribute;
    }

    private void basicProcess(Field field, Object attribute, Class<?> fieldType, String[] paramValues)
                    throws IllegalAccessException {
        if (ConvertUtils.isBasicType(fieldType)) {
            field.set(attribute, ConvertUtils.convertToTargetType(paramValues[0], fieldType));
        } else if (field.getType().isArray()) {
            field.set(attribute, ConvertUtils.convertArrayToTargetType(paramValues, fieldType));
        } else if (List.class.isAssignableFrom(fieldType)) {
            List<Object> list = new ArrayList<>();
            for (String val : paramValues) {
                list.add(ConvertUtils.convertToTargetType(val, fieldType));
            }
            field.set(attribute, list);
        }
    }

    private void decryptProcess(Field field, Object attribute, Encrypt fieldEncrypt, Class<?> fieldType,
                    String[] paramValues) throws IllegalAccessException {
        if (ConvertUtils.isBasicType(fieldType)) {
            field.set(attribute, EncryptUtils.decrypt(paramValues[0], fieldEncrypt, fieldType));
        } else if (field.getType().isArray()) {
            Object[] objects = new Object[paramValues.length];
            for (int i = 0; i < paramValues.length; i++) {
                objects[i] = EncryptUtils.decrypt(paramValues[i], fieldEncrypt, fieldType);
            }
            field.set(attribute, objects);
        } else if (List.class.isAssignableFrom(field.getType())) {
            List<Object> list = new ArrayList<>();
            for (String val : paramValues) {
                list.add(EncryptUtils.decrypt(val, fieldEncrypt, fieldType));
            }
            field.set(attribute, list);
        }
    }
}
