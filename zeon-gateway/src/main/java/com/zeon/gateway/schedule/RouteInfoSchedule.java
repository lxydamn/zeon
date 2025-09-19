package com.zeon.gateway.schedule;

import com.netflix.discovery.EurekaClient;
import com.zeon.gateway.context.GatewayContext;
import com.zeon.web.dao.GatewayRouteDao;
import com.zeon.web.entity.GatewayRoute;
import jakarta.annotation.Resource;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>
 * </p>
 *
 * @author lxy2914344878@163.com
 * @since 2025/9/6 11:31
 */
@Component
public class RouteInfoSchedule {

	private static final Logger LOGGER = LoggerFactory.getLogger(RouteInfoSchedule.class);
	private final Map<String, String> simplifyNameCache = new HashMap<>();

	@Resource
	private EurekaClient eurekaClient;
	@Resource
	private GatewayContext gatewayContext;
	@Resource
	private GatewayRouteDao gatewayRouteDao;
	@Value("${spring.application.name}")
	private String serviceName;

	private String extractSimpleName(String name) {
		if (simplifyNameCache.containsKey(name)) {
			return simplifyNameCache.get(name);
		}
		String[] split = name.toLowerCase().split("-");
		if (split.length == 1) {
			return split[0].substring(0, 4);
		}
		String simpleName = split[0].charAt(0) + split[1].substring(0, 3);
		simplifyNameCache.put(name, simpleName);
		return simpleName;
	}

	private String computeUri(String name) {
		return String.format("lb://%s", name);
	}

	private String computePredicates(String name) {
		return String.format("Path=/%s/**", name);
	}

	@Scheduled(fixedRate = 30, timeUnit = TimeUnit.SECONDS, initialDelay = 30)
	public void fetchServiceInfo() {
		Set<String> set = gatewayContext.getGatewayRouteMap().keySet();
		AtomicBoolean hasNewRoute = new AtomicBoolean(false);
		LOGGER.info("ZeonGateway: Fetching services info from eureka server");
		eurekaClient.getApplications().getRegisteredApplications().forEach(app -> {
			if (StringUtils.equals(app.getName().toLowerCase(), serviceName)) {
				return;
			}
			if (set.contains(app.getName())) {
				return;
			}
			hasNewRoute.set(true);
			GatewayRoute route = GatewayRoute.builder()
					.serviceName(app.getName())
					.uri(computeUri(app.getName()))
					.predicates(computePredicates(extractSimpleName(app.getName())))
					.description("Gateway service auto register")
					.filters("StripPrefix=1")
					.build();
			gatewayRouteDao.insert(route);
		});
		if (hasNewRoute.get()) {
			gatewayContext.refresh();
		}
	}
}
