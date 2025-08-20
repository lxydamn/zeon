package com.zeon.mvc;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.PriorityOrdered;
import org.springframework.util.CollectionUtils;
import org.springframework.web.method.annotation.RequestParamMethodArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.PathVariableMethodArgumentResolver;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeon.json.CryptoModule;
import com.zeon.json.EncryptBeanDeserializerModifier;
import com.zeon.json.EncryptBeanSerializerModifier;

/**
 * <p></p>
 *
 * @author xingyang.li@hand-china.com  2025/8/19 15:49
 */
public class WebBeanPostProcessor implements BeanPostProcessor, PriorityOrdered {
    private static final Logger logger = LoggerFactory.getLogger(WebBeanPostProcessor.class.getName());
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof ObjectMapper objectMapper) {
            logger.info("ObjectMapper registering cryptoModule");
            objectMapper.registerModule(new CryptoModule(new EncryptBeanSerializerModifier(),
                            new EncryptBeanDeserializerModifier()));
        } else if (bean instanceof RequestMappingHandlerAdapter adapter) {
            List<HandlerMethodArgumentResolver> resolvers = adapter.getArgumentResolvers();
            if (CollectionUtils.isEmpty(resolvers)) {
                throw new IllegalStateException("No argument resolvers found");
            }
            List<HandlerMethodArgumentResolver> newResolvers = new ArrayList<>(resolvers.size());
            for (HandlerMethodArgumentResolver resolver : resolvers) {
                System.out.println(resolver);
                if (resolver instanceof RequestParamMethodArgumentResolver) {
                    newResolvers.add(new EncryptRequestParamResolver(false));
                } else if (resolver instanceof PathVariableMethodArgumentResolver) {
                    newResolvers.add(new EncryptPathVariableResolver());
                    newResolvers.add(new EncryptModelAttributeResolver());
                } else {
                    newResolvers.add(resolver);
                }
            }
            adapter.setArgumentResolvers(newResolvers);
            System.out.println(adapter.getArgumentResolvers());
		}
		return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
	}

	@Override
	public int getOrder() {
		return Integer.MIN_VALUE;
	}
}
