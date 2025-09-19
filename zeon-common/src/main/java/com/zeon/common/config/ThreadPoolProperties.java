package com.zeon.common.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

/**
 * <p>
 * </p>
 *
 * @author lxy2914344878@163.com
 * @since 2025/9/9 20:19
 */

@Data
@Configuration
@ConfigurationProperties("threadpool")
public class ThreadPoolProperties {
    /**
     * 核心线程数，默认为CPU核心数
     */
    private int coreSize = Runtime.getRuntime().availableProcessors();

    /**
     * 最大线程数，默认为核心线程数的2倍
     */
    private int maxSize = Runtime.getRuntime().availableProcessors() * 2;

    /**
     * 队列容量，默认为100
     */
    private int queueCapacity = 100;

    /**
     * 线程空闲时间（秒），默认为60秒
     */
    private int keepAliveSeconds = 60;

    /**
     * 线程名称前缀，默认为"zeon-executor-"
     */
    private String threadNamePrefix = "zeon-executor-";
}
