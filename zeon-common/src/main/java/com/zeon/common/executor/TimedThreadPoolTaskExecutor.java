package com.zeon.common.executor;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 支持任务计时的线程池执行器
 *
 * @author lxy2914344878@163.com
 * @since 2025/9/9 21:00
 */
public class TimedThreadPoolTaskExecutor extends ThreadPoolTaskExecutor {

    private static final Logger logger = LoggerFactory.getLogger(TimedThreadPoolTaskExecutor.class);

    /**
     * 提交带计时功能的Runnable任务
     */
    @Override
    public void execute(Runnable task) {
        super.execute(new TimedRunnable(task));
    }

    /**
     * 提交带计时功能的Runnable任务
     */
    @Override
    public Future<?> submit(Runnable task) {
        return super.submit(new TimedRunnable(task));
    }

    /**
     * 提交带计时功能的Callable任务
     */
    @Override
    public <T> Future<T> submit(Callable<T> task) {
        return super.submit(new TimedCallable<>(task));
    }

    /**
     * 记录任务执行时间的通用方法
     * 
     * @param startTime 开始时间（纳秒）
     * @param threadName 线程名称
     */
    private void logTaskExecution(long startTime, String threadName) {
        long endTime = System.nanoTime();
        long duration = endTime - startTime;
        double durationInMillis = duration / 1_000_000.0;

        if (logger.isInfoEnabled()) {
            // 使用字符串拼接替代String.format提高性能
            logger.info("[{}] 任务执行完成，耗时: {} ms", threadName, String.format("%.2f", durationInMillis));
        }
    }

    /**
     * 带计时功能的Runnable包装类
     */
    private class TimedRunnable implements Runnable {
        private final Runnable task;

        public TimedRunnable(Runnable task) {
            this.task = task;
        }

        @Override
        public void run() {
            long startTime = System.nanoTime();
            String threadName = Thread.currentThread().getName();

            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("[{}] 任务开始执行", threadName);
                }
                task.run();
            } finally {
                logTaskExecution(startTime, threadName);
            }
        }
    }

    /**
     * 带计时功能的Callable包装类
     */
    private class TimedCallable<T> implements Callable<T> {
        private final Callable<T> task;

        public TimedCallable(Callable<T> task) {
            this.task = task;
        }

        @Override
        public T call() throws Exception {
            long startTime = System.nanoTime();
            String threadName = Thread.currentThread().getName();

            try {
                if (logger.isDebugEnabled()) {
                    logger.debug("[{}] 任务开始执行", threadName);
                }
                return task.call();
            } finally {
                logTaskExecution(startTime, threadName);
            }
        }
    }
}
