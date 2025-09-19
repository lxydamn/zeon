package com.zeon.common.config;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.zeon.common.executor.TimedThreadPoolTaskExecutor;

/**
 * <p>
 * </p>
 *
 * @author lxy2914344878@163.com
 * @since 2025/9/9 20:18
 */
@Configuration
@EnableScheduling
public class ThreadPoolConfig {

    @Autowired
    private ThreadPoolProperties properties;
    private ThreadPoolTaskExecutor executor;

    @Bean("commonExecutor")
    public Executor commonExecutor() {
        TimedThreadPoolTaskExecutor executor = new TimedThreadPoolTaskExecutor();
        executor.setCorePoolSize(properties.getCoreSize());
        executor.setMaxPoolSize(properties.getMaxSize());
        executor.setQueueCapacity(properties.getQueueCapacity());
        executor.setKeepAliveSeconds(properties.getKeepAliveSeconds());
        executor.setThreadNamePrefix(properties.getThreadNamePrefix());

        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        executor.initialize();
        this.executor = executor;
        return executor;
    }

    @Scheduled(fixedRate = 60, timeUnit = TimeUnit.SECONDS)
    public void printStatus() {
        ThreadPoolExecutor poolExecutor = executor.getThreadPoolExecutor();
        System.out.println("<========== Common executor status ==========>");
        System.out.printf("Live thread %d, queue size %d, completed size %d, total task %d%n",
                        poolExecutor.getActiveCount(), poolExecutor.getQueue().size(),
                        poolExecutor.getCompletedTaskCount(), poolExecutor.getTaskCount());
    }
}
