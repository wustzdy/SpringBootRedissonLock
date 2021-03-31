package com.wustzdy.redissondemo.test;

import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//https://blog.csdn.net/w372426096/article/details/103761286?utm_medium=distribute.pc_relevant_t0.none-task-blog-2%7Edefault%7EBlogCommendFromMachineLearnPai2%7Edefault-1.control&dist_request_id=&depth_1-utm_source=distribute.pc_relevant_t0.none-task-blog-2%7Edefault%7EBlogCommendFromMachineLearnPai2%7Edefault-1.control
public class LockTest2 {
    public static void main(String[] args) {

        // 1.构造redisson实现分布式锁必要的Config
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + "127.0.0.1" + ":" + "6379").setPassword(null);
        // 2.构造RedissonClient
        RedissonClient redissonClient = Redisson.create(config);
        // 3.获取锁对象实例（无法保证是按线程的顺序获取到）
        RLock rLock = redissonClient.getLock("lock_zdy_test");
        ExecutorService executorService = Executors.newFixedThreadPool(5);
        for (int i = 0; i < 10; i++) {
            executorService.submit(() -> {
                try {
                    /**
                     * 4.尝试获取锁
                     * waitTimeout 尝试获取锁的最大等待时间，超过这个值，则认为获取锁失败
                     * leaseTime   锁的持有时间,超过这个时间锁会自动失效（值应设置为大于业务处理的时间，确保在锁有效期内业务能处理完）
                     */
                    boolean res = rLock.tryLock((long) 10, (long) 15, TimeUnit.SECONDS);
                    if (res) {
                        //成功获得锁，在这里处理业务
                        System.out.println("线程 " + Thread.currentThread().getId() + " 获得锁：" + System.currentTimeMillis());
                    }
                    Thread.sleep(40);
                } catch (Exception e) {
                    throw new RuntimeException("aquire lock fail");
                } finally {
                    System.out.println("线程" + Thread.currentThread().getId() + "释放锁：" + System.currentTimeMillis());
//                    System.out.print("是否还持有锁：" + rLock.isLocked());
                    /*if (rLock.isLocked()) {
                        rLock.forceUnlock();
                    }*/
                }
            });
        }
    }
}
