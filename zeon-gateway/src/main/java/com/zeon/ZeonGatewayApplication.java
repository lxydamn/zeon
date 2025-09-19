package com.zeon;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * <p>
 * </p>
 *
 * @author lxy2914344878@163.com
 * @since 2025/9/6 10:30
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableScheduling
public class ZeonGatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZeonGatewayApplication.class, args);
	}
}
