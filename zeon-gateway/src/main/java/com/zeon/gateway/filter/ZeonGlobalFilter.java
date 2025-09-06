package com.zeon.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

/**
 * <p>全局过滤器，用于拦截所有经过网关的请求</p>
 *
 * @author lxy2914344878@163.com
 * @since 2025/9/6 13:46
 */
@Component
public class ZeonGlobalFilter implements GlobalFilter, Ordered {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZeonGlobalFilter.class);

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();

		// 记录请求信息
		LOGGER.info("网关拦截请求: {} {}", request.getMethod(), request.getURI());

		// 继续执行过滤器链，并在完成后记录响应
		return chain.filter(exchange).then(
				Mono.fromRunnable(() -> {
					ServerHttpResponse response = exchange.getResponse();
					LOGGER.info("网关响应状态: {} for {} {}", response.getStatusCode(),
							request.getMethod(), request.getURI());
				})
		);
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}
}
