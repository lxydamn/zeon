package com.zeon.gateway.service;

import com.zeon.web.entity.GatewayRoute;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.event.RefreshRoutesEvent;
import org.springframework.cloud.gateway.filter.FilterDefinition;
import org.springframework.cloud.gateway.handler.predicate.PredicateDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinitionRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * <p> </p>
 *
 * @author lxy2914344878@163.com
 * @since 2025/9/6 13:25
 */
@Component
public class DynamicRouteService {
	private static final Logger LOGGER = LoggerFactory.getLogger(DynamicRouteService.class);
	@Resource
	private RouteDefinitionRepository repository;
	@Resource
	private ApplicationEventPublisher publisher;

	public List<RouteDefinition> generateRouteDefinition(Set<GatewayRoute> routes) {
		// 从上下文中获取路由信息
		if (CollectionUtils.isEmpty(routes)) {
			return Collections.emptyList();
		}

		List<RouteDefinition> routeDefinitions = new ArrayList<>();
		for (GatewayRoute gatewayRoute : routes) {
			try {
				RouteDefinition routeDefinition = new RouteDefinition();
				routeDefinition.setId(gatewayRoute.getServiceName());
				routeDefinition.setUri(new URI(gatewayRoute.getUri()));
				// 解析predicates
				routeDefinition.setPredicates(parseConfig(gatewayRoute.getPredicates()));
				// 解析filters
				routeDefinition.setFilters(parseFilters(gatewayRoute.getFilters()));
				// 设置顺序
				routeDefinition.setOrder(gatewayRoute.getOrderBy() != null ? gatewayRoute.getOrderBy() : 0);

				routeDefinitions.add(routeDefinition);
			} catch (URISyntaxException e) {
				LOGGER.error("ZeonGateway: URI syntax error, {}", e.getMessage());
			}
		}
		return routeDefinitions;
	}

	public void addRoute(Set<GatewayRoute> routes) {
		List<RouteDefinition> generatedRouteDefinition = generateRouteDefinition(routes);
		for (RouteDefinition routeDefinition : generatedRouteDefinition) {
			repository.save(Mono.just(routeDefinition)).subscribe();
		}
		LOGGER.info("ZeonGateway: Publisher add route event, {}",
				routes.stream().map(GatewayRoute::getServiceName).toList());
		publisher.publishEvent(new RefreshRoutesEvent(this));
	}

	public void deleteRoute(Set<GatewayRoute> routes) {
		List<RouteDefinition> generatedRouteDefinition = generateRouteDefinition(routes);
		for (RouteDefinition routeDefinition : generatedRouteDefinition) {
			repository.delete(Mono.just(routeDefinition.getId())).subscribe();
		}
		LOGGER.info("ZeonGateway: Publisher delete route event, {}",
				routes.stream().map(GatewayRoute::getServiceName).toList());
		publisher.publishEvent(new RefreshRoutesEvent(this));
	}

	private List<FilterDefinition> parseFilters(String filters) {
		String[] split = filters.split(";");
		List<FilterDefinition> arrayList = new ArrayList<>();
		for (String s : split) {
			FilterDefinition filterDefinition = new FilterDefinition(s);
			arrayList.add(filterDefinition);
		}
		return arrayList;
	}

	private List<PredicateDefinition> parseConfig(String config) {
		String[] split = config.split(";");
		List<PredicateDefinition> arrayList = new ArrayList<>();
		for (String s : split) {
			PredicateDefinition predicateDefinition = new PredicateDefinition(s);
			arrayList.add(predicateDefinition);
		}
		return arrayList;
	}
}
