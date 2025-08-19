package com.zeon.mvc;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.PriorityOrdered;
import org.springframework.http.converter.HttpMessageConverter;
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
			// 对 ObjectMapper 进行自定义配置，添加加密序列化器
            logger.info("ObjectMapper init: {}, {}", objectMapper.getClass().getName(), objectMapper);

			// 注册自定义的序列化模块，用于处理加密字段
            objectMapper.registerModule(new CryptoModule(new EncryptBeanSerializerModifier(),
                            new EncryptBeanDeserializerModifier()));
        } else if (bean instanceof RequestMappingHandlerAdapter adapter) {
			// 对 RequestMappingHandlerAdapter 进行自定义配置，设置加密的消息转换器
            logger.info("RequestMappingHandlerAdapter init: {}", adapter.getClass().getName());
			List<HttpMessageConverter<?>> converters = adapter.getMessageConverters();
            converters.forEach(converter -> {
                logger.info("adapter converter:{}", converter.getClass().getName());

            });
		}
		return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
	}

	@Override
	public int getOrder() {
		return Integer.MIN_VALUE;
	}
}
