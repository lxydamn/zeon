package com.zeon.encrypt.mvc;

import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;

import com.zeon.encrypt.core.Encrypt;
import com.zeon.encrypt.utils.EncryptUtils;

/**
 * @author lxy
 */
public class EncryptRequestParamResolver extends RequestParamMethodArgumentResolver {

    public EncryptRequestParamResolver(boolean useDefaultResolution) {
        super(useDefaultResolution);
    }

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
