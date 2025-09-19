package com.zeon.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.Collections;

/**
 * 跨域配置类
 *
 * @author lxy2914344878@163.com
 * @since 2025/9/7
 */
@Configuration
public class CorsConfig {

	@Bean
	public CorsWebFilter corsWebFilter() {
		CorsConfiguration config = new CorsConfiguration();
		// 允许的域名,可以使用*表示允许所有域名
		config.setAllowedOriginPatterns(Collections.singletonList("*"));
		// 允许的请求头
		config.setAllowedHeaders(Collections.singletonList("*"));
		// 允许的请求方法
		config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		// 是否允许携带认证信息（如cookies）
		config.setAllowCredentials(true);
		// 预检请求的有效期，单位为秒
		config.setMaxAge(3600L);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", config);

		return new CorsWebFilter(source);
	}
}
