package com.wustzdy.redissondemo.test;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.util.StringUtils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LockTest {
    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + "127.0.0.1" + ":" + "6379");
        // 如果密码为空
        config.useSingleServer().setPassword(null);
        RedissonClient client = Redisson.create(config);
        RLock lock = client.getLock("lock");

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                try {
                    lock.tryLock(-1, 60, TimeUnit.SECONDS);
                    System.out.println("线程 " + Thread.currentThread().getId() + " 获得锁：" + System.currentTimeMillis());
                    Thread.sleep(4000);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    System.out.println("线程" + Thread.currentThread().getId() + "释放锁：" + System.currentTimeMillis());
                    lock.forceUnlock();
                }
            });
        }
        System.out.println(lock.getName());
        System.out.println(lock.getHoldCount());
    }
}
