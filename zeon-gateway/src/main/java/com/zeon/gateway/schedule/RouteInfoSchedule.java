package com.zeon.gateway.schedule;

import com.netflix.discovery.EurekaClient;
import com.zeon.gateway.context.GatewayContext;
import com.zeon.web.entity.GatewayRoute;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	@Scheduled(fixedRate = 180000)
	public void fetchServiceInfo() {
		LOGGER.info("ZeonGateway: Fetching services info from eureka server");
		List<GatewayRoute> list = eurekaClient.getApplications().getRegisteredApplications().stream().map(
				app -> GatewayRoute.builder()
						.uri(computeUri(app.getName()))
						.serviceName(app.getName())
						.predicates(computePredicates(extractSimpleName(app.getName())))
						.filters("StripPrefix=1")
						.build()
		).toList();

		gatewayContext.reset(list);
	}
}
