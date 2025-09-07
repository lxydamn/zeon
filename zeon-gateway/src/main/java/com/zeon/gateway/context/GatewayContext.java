package com.zeon.gateway.context;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.zeon.gateway.service.DynamicRouteService;
import com.zeon.web.dao.GatewayRouteDao;
import com.zeon.web.entity.GatewayRoute;
import jakarta.annotation.Resource;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 网关上下文，管理路由配置
 * </p>
 *
 * @author lxy2914344878@163.com
 * @since 2025/9/6 11:21
 */
@Setter
@Getter
@Component
public class GatewayContext {
	private final static Logger LOGGER = LoggerFactory.getLogger(GatewayContext.class);
	@Resource
	private GatewayRouteDao gatewayRouteDao;
	@Resource
	private DynamicRouteService dynamicRouteService;

	public Set<GatewayRoute> routes = new HashSet<>();
	public Map<String, GatewayRoute> gatewayRouteMap = new HashMap<>();

	public void reset(List<GatewayRoute> gatewayRoutes) {
		Set<GatewayRoute> newGateway = new HashSet<>(gatewayRoutes);

		Set<GatewayRoute> delSet = routes.stream()
				.filter(route -> !newGateway.contains(route))
				.collect(Collectors.toSet());
		Set<GatewayRoute> addSet = newGateway.stream()
				.filter(route -> !routes.contains(route))
				.collect(Collectors.toSet());
		// 无变化不更新
		if (delSet.isEmpty() && addSet.isEmpty()) {
			return;
		}
		// 更新并发起网关刷新
		this.routes = new HashSet<>(gatewayRoutes);
		this.gatewayRouteMap = gatewayRoutes.stream()
				.collect(Collectors
						.toMap(GatewayRoute::getServiceName,
								route -> route));
		if (!delSet.isEmpty()) {
			dynamicRouteService.deleteRoute(delSet);
		}
		if (!addSet.isEmpty()) {
			dynamicRouteService.addRoute(addSet);
		}
	}

	public void refresh() {
		this.reset(gatewayRouteDao.selectList(null));
	}

	@EventListener(ContextRefreshedEvent.class)
	public void initializeGatewayRoutes() {
		LOGGER.info("Context loaded, starting read gateway routes from database");
		List<GatewayRoute> gatewayRoutes = gatewayRouteDao.selectList(new LambdaQueryWrapper<>(GatewayRoute.class)
				.eq(GatewayRoute::getEnabled, Boolean.TRUE));
		this.reset(gatewayRoutes);
	}
}