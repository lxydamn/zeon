package com.zeon.export.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

import com.zeon.export.annotations.Export;
import com.zeon.export.handler.SyncExportHandler;

/**
 * <p>
 * </p>
 *
 * @author lxy2914344878@163.com
 * @since 2025/9/3 10:29
 */
@Aspect
@Component
public class ExportAroundAspect implements InitializingBean {
    private final static Logger LOGGER = LoggerFactory.getLogger(ExportAroundAspect.class);

    @Around(value = "@annotation(export)")
    public Object wrapResponse(ProceedingJoinPoint joinPoint, Export export) throws Throwable {
        switch (export.method()) {
            case SYNC:
                Object result = joinPoint.proceed();
                SyncExportHandler.handle(result, export);
                break;
            case ASYNC:
                break;
            default:
                return joinPoint.proceed();
        }
        return null;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        LOGGER.info("<================= Export aspect successfully loaded ==================>");
    }
}
