package com.zeon.export.aspect;

import java.util.Collection;
import java.util.concurrent.Executor;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.alibaba.fastjson2.JSONArray;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeon.export.annotations.Export;
import com.zeon.export.constants.ExportMethod;
import com.zeon.export.handler.AsyncExportHandler;


/**
 * <p>
 * </p>
 *
 * @author lxy2914344878@163.com
 * @since 2025/9/9 19:39
 */
@Aspect
@Component
public class ExportAroundAspect {

    private final Executor executor;
    private final ObjectMapper objectMapper;

    public ExportAroundAspect(Executor commonExecutor, ObjectMapper objectMapper) {
        this.executor = commonExecutor;
        this.objectMapper = objectMapper;
    }

    @Around("@annotation(export)")
    public Object around(ProceedingJoinPoint joinPoint, Export export) throws Throwable {
        // 该切面不处理同步导出
        if (export.method().equals(ExportMethod.SYNC)) {
            return joinPoint.proceed();
        }
        String taskId = AsyncExportHandler.writeAsyncResponse();
        executor.execute(() -> {
            try {
                Object result = extractData(joinPoint.proceed());
                String json = objectMapper.writeValueAsString(result);
                AsyncExportHandler.handle(JSONArray.parse(json), export, taskId);
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });
        return null;
    }

    private Object extractData(Object result) {
        if (result instanceof ResponseEntity<?> responseEntity) {
            return responseEntity.getBody();
        }
        if (result instanceof Collection<?> collection) {
            return collection;
        }
        throw new RuntimeException("Results is not support to transfer xlsx");
    }
}
