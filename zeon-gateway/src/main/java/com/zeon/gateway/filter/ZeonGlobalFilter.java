package com.zeon.gateway.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.UUID;

/**
 * <p>全局过滤器，用于拦截所有经过网关的请求</p>
 *
 * @author lxy2914344878@163.com
 * @since 2025/9/6 13:46
 */
@Component
public class ZeonGlobalFilter implements GlobalFilter, Ordered {
	private static final Logger LOGGER = LoggerFactory.getLogger(ZeonGlobalFilter.class);

	private String generateTranceId() {
		return String.format("%s-%s", System.currentTimeMillis(),
				UUID.randomUUID().toString().replace("-", "").substring(0, 16));
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		ServerHttpRequest request = exchange.getRequest();

		LOGGER.info("GlobalFilter fetch request: {} {}", request.getMethod(), request.getURI());
		HttpHeaders headers = request.getHeaders();
		headers.add("X-Request-Id", generateTranceId());

		return chain.filter(exchange);
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}
}
