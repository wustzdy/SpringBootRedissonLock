package com.wustzdy.redissondemo.test;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LockTest2 {
    public static void main(String[] args) {

        // 1.构造redisson实现分布式锁必要的Config
        Config config = new Config();
        config.useSingleServer().setAddress("redis://127.0.0.1:5379").setPassword("123456").setDatabase(0);
        // 2.构造RedissonClient
        RedissonClient redissonClient = Redisson.create(config);
        // 3.获取锁对象实例（无法保证是按线程的顺序获取到）
        RLock rLock = redissonClient.getLock("lock_zdy_test");
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                try {
                    boolean res = rLock.tryLock((long) 10, (long) 15, TimeUnit.SECONDS);
                    if (res) {
                        //成功获得锁，在这里处理业务
                        System.out.println("线程 " + Thread.currentThread().getId() + " 获得锁：" + System.currentTimeMillis());
                    }
                } catch (Exception e) {
                    throw new RuntimeException("aquire lock fail");
                } finally {
                    System.out.println("线程" + Thread.currentThread().getId() + "释放锁：" + System.currentTimeMillis());
//                    rLock.forceUnlock();
                }
            });
        }
    }
}
