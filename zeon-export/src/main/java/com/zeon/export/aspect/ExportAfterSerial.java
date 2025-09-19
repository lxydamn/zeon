package com.zeon.export.aspect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.lang.NonNull;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import com.alibaba.fastjson2.JSONArray;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeon.export.annotations.Export;
import com.zeon.export.handler.SyncExportHandler;

import jakarta.annotation.Resource;

/**
 * <p>
 * </p>
 *
 * @author lxy2914344878@163.com
 * @since 2025/9/7 19:19
 */
@ControllerAdvice
public class ExportAfterSerial implements ResponseBodyAdvice<Object> {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExportAfterSerial.class);
    @Resource
    private ApplicationContext applicationContext;

    @Override
    public boolean supports(MethodParameter returnType,
                    @NonNull Class<? extends HttpMessageConverter<?>> converterType) {
        return returnType.hasMethodAnnotation(Export.class);
    }

    @Override
    public Object beforeBodyWrite(Object body, @NonNull MethodParameter returnType,
                    @NonNull MediaType selectedContentType,
                    @NonNull Class<? extends HttpMessageConverter<?>> converterType, @NonNull ServerHttpRequest request,
                    @NonNull ServerHttpResponse response) {
        if (body == null) {
            return null;
        }
        HttpMessageConverter<?> converter = applicationContext.getBean(converterType);
        if (!(converter instanceof MappingJackson2HttpMessageConverter jacksonConverter)) {
            return body;
        }
        Export export = returnType.getMethodAnnotation(Export.class);
        Assert.notNull(export, "export annotation is null");
        ObjectMapper objectMapper = jacksonConverter.getObjectMapper();
        try {
            String s = objectMapper.writeValueAsString(body);
            JSONArray json = JSONArray.parse(s);
            SyncExportHandler.handle(json, export);
        } catch (JsonProcessingException e) {
            LOGGER.error("json error: {}", e.getMessage());
        }

        return null;
    }
}
