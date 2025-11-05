package com.zeon.common.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Collections;
import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicLong;

@Slf4j
@Component
public class RedisLockUtils {

	@Autowired
	private StringRedisTemplate template;

	// key -> stop
	private final ConcurrentHashMap<String, AtomicBoolean> watchdogMap = new ConcurrentHashMap<>();
	// key -> scheduledFuture
	private final ConcurrentHashMap<String, ScheduledFuture<?>> watchdogFutureMap = new ConcurrentHashMap<>();

	private static final String UNLOCK_LUA =
			"if redis.call('get', KEYS[1]) == ARGV[1] then " +
			"return redis.call('del', KEYS[1]) " +
			"else return 0 end";

	private final AtomicLong watchdogCount = new AtomicLong();

	private static final DefaultRedisScript<Long> UNLOCK_SCRIPT;

	private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(8, r -> {
		Thread t = new Thread(r);
		t.setName("redis-lock-watchdog-" + watchdogCount.updateAndGet(i -> i >= 100000000000L ? 1L : i + 1));
		return t;
	});

	static {
		UNLOCK_SCRIPT = new DefaultRedisScript<>();
		UNLOCK_SCRIPT.setScriptText(UNLOCK_LUA);
		UNLOCK_SCRIPT.setResultType(Long.class);
	}

	private String getUUID() {
		return UUID.randomUUID().toString();
	}

	private void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 关闭看门狗任务
	 */
	private void stop(String key) {
		// 停止标志
		AtomicBoolean atomicBoolean = watchdogMap.remove(key);
		if (atomicBoolean != null) {
			atomicBoolean.set(true);
		}

		// 取消调度任务
		ScheduledFuture<?> future = watchdogFutureMap.remove(key);
		if (future != null) {
			boolean cancelled = future.cancel(true);
			if (cancelled) {
				log.info("Watchdog for key {} cancelled successfully", key);
			} else {
				log.warn("Failed to cancel watchdog for key {}", key);
			}
		}
	}


	private void startWatchdog(String key, String value, Duration ttl, Duration maxLockTime, String parentThreadName) {
		AtomicBoolean stop = new AtomicBoolean(false);
		Duration refreshInterval = Duration.ofMillis(Math.max(100L, ttl.toMillis() / 3));
		long maxAliveTime = System.currentTimeMillis() + maxLockTime.toMillis();
		watchdogMap.put(key, stop);
		ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> {
			try {
				if (stop.get()) {
					log.info("[{}] lock key {} watchdog stopped", parentThreadName, key);
					stop(key);
					return;
				}

				if (System.currentTimeMillis() > maxAliveTime) {
					log.info("[{}] lock key {} expired, auto unlock", parentThreadName, key);
					unlock(key, value);
					stop(key);
					return;
				}

				String currentVal = template.opsForValue().get(key);
				if (Objects.equals(currentVal, value)) {
					log.info("[{}] watchdog refreshing lock key {}, ttl {}", parentThreadName, key, ttl);
					template.expire(key, ttl);
				} else {
					log.info("[{}] watchdog detected invalid lock key {}, stopping", parentThreadName, key);
					stop(key);
				}
			} catch (Exception e) {
				log.error("Watchdog task error for key {}", key, e);
			}
		}, refreshInterval.toMillis(), refreshInterval.toMillis(), TimeUnit.MILLISECONDS);

		watchdogFutureMap.put(key, future);
	}

	public String tryLock(String key, Duration ttl, Duration waitTime, Duration maxLockTime) {
		String value = getUUID();
		int attempts = 0;
		while (attempts < 3) {
			Boolean success = template.opsForValue().setIfAbsent(key, value, ttl);
			if (Boolean.TRUE.equals(success)) {
				log.info("Thread {} get lock {}", Thread.currentThread().getName(), key);
				// 启动看门狗线程
				startWatchdog(key, value, ttl, maxLockTime, Thread.currentThread().getName());
				return value;
			} else {
				attempts ++;
				log.info("Thread {} get lock {} failed, retrying...", Thread.currentThread().getName(), key);
				sleep(waitTime.toMillis());
			}

		}
		return null;
	}

	public String tryLock(String key, Duration ttl) {
		return tryLock(key, ttl, Duration.ofMillis(3000), Duration.ofMinutes(3));
	}

	public String tryLock(String key) {
		return tryLock(key, Duration.ofMillis(3000));
	}

	public String tryLock(String key, Duration ttl, Duration waitTime) {
		return tryLock(key, ttl, waitTime, Duration.ofMinutes(3));
	}

	public boolean unlock(String key, String value) {
		log.info("Thread {} unlock {}", Thread.currentThread().getName(), key);
		stop(key);
		return template.execute(UNLOCK_SCRIPT, Collections.singletonList(key), value) == 1;
	}
}
