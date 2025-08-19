package com.zeon;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zeon.mvc.WebBeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p></p>
 *
 * @author xingyang.li@hand-china.com  2025/8/19 16:54
 */
@Configuration
public class EncryptConfiguration {
	@Bean
	public WebBeanPostProcessor webBeanPostProcessor() {
		return new WebBeanPostProcessor();
	}

	@Bean
	public ObjectMapper objectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();

		return objectMapper;
	}
}
