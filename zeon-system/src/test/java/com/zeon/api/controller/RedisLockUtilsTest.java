package com.zeon.api.controller;

import com.zeon.common.utils.RedisLockUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;
/**
 * <p> </p>
 *
 * @author lxy2914344878@163.com
 * @since 2025/11/4 9:46
 *
 */
@SpringBootTest
class RedisLockUtilsTest {

	@Autowired
	private RedisLockUtils redisLockUtils;
	@Test
	void testBasicLockUnlock() {
		String key = "test-lock-key";
		Duration ttl = Duration.ofSeconds(10);

		// 获取锁
		String lockValue = redisLockUtils.tryLock(key, ttl);
		System.out.println(lockValue);
		assertNotNull(lockValue, "应该能够成功获取锁");
		// 尝试再次获取同一个锁（应该失败）
		String lockValue2 = redisLockUtils.tryLock(key, ttl);
		System.out.println(lockValue2);
		assertNull(lockValue2, "同一把锁不应该被重复获取");

		// 解锁
		boolean unlocked = redisLockUtils.unlock(key, lockValue);
		System.out.println(unlocked);
		assertTrue(unlocked, "应该能够成功解锁");

		// 再次尝试获取锁（应该成功）
		String lockValue3 = redisLockUtils.tryLock(key, ttl);
		System.out.println(lockValue3);
		assertNotNull(lockValue3, "解锁后应该能够重新获取锁");
		boolean unlock = redisLockUtils.unlock(key, lockValue3);
		System.out.println(unlock);
	}

	@Test
	void testConcurrentLockAccess() throws InterruptedException {
		String key = "concurrent-lock-test";
		Duration ttl = Duration.ofSeconds(30);
		AtomicInteger successCount = new AtomicInteger(0);

		// 创建多个线程同时竞争同一把锁
		for (int i = 0; i < 10; i++) {
			Thread thread = new Thread(() -> {
				String value = redisLockUtils.tryLock(key, ttl, Duration.ofSeconds(1));
				if (value != null) {
					successCount.incrementAndGet();
					// 持有锁一段时间
					try {
						Thread.sleep(100L);
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
					redisLockUtils.unlock(key, value);
				} else {
					System.out.println(Thread.currentThread().getName() + ": 获取锁失败");
				}
			});
			thread.setName("Test-Thread-" + i);
			Thread.sleep((long) Math.random() * 1000L);
			thread.start();
		}

		Thread.sleep(60000);
		System.out.println("成功获取锁的线程数：" + successCount.get());
	}
}