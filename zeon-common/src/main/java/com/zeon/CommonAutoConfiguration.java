package com.zeon;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

/**
 * <p>
 * </p>
 *
 * @author lxy2914344878@163.com
 * @since 2025/9/9 20:10
 */
@EnableAsync
@AutoConfiguration
@ComponentScan(basePackages = "com.zeon.common")
public class CommonAutoConfiguration {
}
