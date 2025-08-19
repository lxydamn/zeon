package com.zeon.mvc;

import java.util.List;
import java.util.logging.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.PriorityOrdered;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

/**
 * <p></p>
 *
 * @author xingyang.li@hand-china.com  2025/8/19 15:49
 */
public class WebBeanPostProcessor implements BeanPostProcessor, PriorityOrdered {
	private static final Logger logger = Logger.getLogger(WebBeanPostProcessor.class.getName());
	@Override
	public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
		if (bean instanceof RestTemplate) {
			RestTemplate restTemplate = (RestTemplate) bean;
			List<HttpMessageConverter<?>> converters = restTemplate.getMessageConverters();
			logger.info("RestTemplate init");
			// 清除现有的转换器并添加自定义的加密转换器
			converters.clear();
			converters.add(new EncryptingHttpMessageConverter());
			converters.forEach(converter -> {
				logger.info("converter:" + converter.getClass().getName());
			});
		} else if (bean instanceof ObjectMapper) {
			ObjectMapper objectMapper = (ObjectMapper) bean;
			// 对 ObjectMapper 进行自定义配置，添加加密序列化器
			logger.info("ObjectMapper init: " + objectMapper.getClass().getName());
			// 注册自定义的序列化模块，用于处理加密字段
			objectMapper.registerModule(new EncryptionModule());
		} else if (bean instanceof RequestMappingHandlerAdapter) {
			RequestMappingHandlerAdapter adapter = (RequestMappingHandlerAdapter) bean;
			// 对 RequestMappingHandlerAdapter 进行自定义配置，设置加密的消息转换器
			logger.info("RequestMappingHandlerAdapter init: " + adapter.getClass().getName());
			List<HttpMessageConverter<?>> converters = adapter.getMessageConverters();
			converters.clear();
			converters.add(new EncryptingHttpMessageConverter());
		}
		return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
	}

	@Override
	public int getOrder() {
		return Integer.MIN_VALUE;
	}
}
