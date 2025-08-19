package com.zeon.mvc;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.util.StreamUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;

public class EncryptingHttpMessageConverter implements HttpMessageConverter<Object> {

    @Override
    public boolean canRead(Class<?> clazz, MediaType mediaType) {
        // 判断是否可以读取该类型的数据
        return true;
    }

    @Override
    public boolean canWrite(Class<?> clazz, MediaType mediaType) {
        // 判断是否可以写入该类型的数据
        return true;
    }

    @Override
    public List<MediaType> getSupportedMediaTypes() {
        // 返回支持的媒体类型
        return Collections.singletonList(MediaType.APPLICATION_JSON);
    }

    @Override
    public Object read(Class<?> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        // 读取并解密请求体
        String encryptedData = StreamUtils.copyToString(inputMessage.getBody(), StandardCharsets.UTF_8);
        String decryptedData = decrypt(encryptedData);
        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(decryptedData, clazz);
    }

    @Override
    public void write(Object o, MediaType contentType, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        // 加密并写入响应体
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonData = objectMapper.writeValueAsString(o);
        String encryptedData = encrypt(jsonData);
        StreamUtils.copy(encryptedData, StandardCharsets.UTF_8, outputMessage.getBody());
    }

    private String encrypt(String data) {
        // 实现你的加密逻辑
        return data;
    }

    private String decrypt(String data) {
        // 实现你的解密逻辑
        return data;
    }
}
