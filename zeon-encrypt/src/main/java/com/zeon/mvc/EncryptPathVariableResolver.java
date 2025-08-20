package com.zeon.mvc;

import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver;

import com.zeon.core.Encrypt;
import com.zeon.utils.EncryptUtils;

/**
 * @author lxy
 *
 */
public class EncryptPathVariableResolver extends PathVariableMethodArgumentResolver {

    @Override
    protected Object resolveName(@NonNull String name, @NonNull MethodParameter parameter,
                    @NonNull NativeWebRequest request) throws Exception {
        Object result = super.resolveName(name, parameter, request);
        Encrypt encrypt = parameter.getParameterAnnotation(Encrypt.class);
        Class<?> targetType = parameter.getParameterType();

        if (encrypt == null || result == null) {
            return result;
        }

        return EncryptUtils.decrypt(result, encrypt, targetType);
    }
}
