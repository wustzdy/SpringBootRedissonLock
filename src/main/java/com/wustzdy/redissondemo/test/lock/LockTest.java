package com.wustzdy.redissondemo.test.lock;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.slf4j.Logger;

import java.util.concurrent.TimeUnit;

@Slf4j
public class LockTest {
    public static void main(String[] args) {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://" + "127.0.0.1" + ":" + "6379").setPassword(null);
        // 2.构造RedissonClient
        RedissonClient redissonClient = Redisson.create(config);

        Thread one = new Thread(new TwoThread(redissonClient));
        Thread two = new Thread(new TwoThread(redissonClient));
        Thread three = new Thread(new TwoThread(redissonClient));
        Thread four = new Thread(new TwoThread(redissonClient));
        one.start();
        two.start();
        three.start();
        four.start();
    }

    private static class TwoThread implements Runnable {
        private RedissonClient redisson;

        public TwoThread(RedissonClient redisson) {
            this.redisson = redisson;
        }

        public void run() {
            RLock lock = redisson.getLock("wustzdy_lock_test");
            try {
                /**
                 * 4.尝试获取锁
                 * waitTimeout 尝试获取锁的最大等待时间，超过这个值，则认为获取锁失败
                 * leaseTime   锁的持有时间,超过这个时间锁会自动失效（值应设置为大于业务处理的时间，确保在锁有效期内业务能处理完）
                 */
                boolean res = lock.tryLock((long) 10, (long) 15, TimeUnit.SECONDS);
                if (res) {
                    //成功获得锁，在这里处理业务
                    System.out.println("线程 " + Thread.currentThread().getId() + " 获得锁：" + System.currentTimeMillis());
                }
            } catch (Exception e) {
                throw new RuntimeException("aquire lock fail");
            } finally {
                System.out.println("线程" + Thread.currentThread().getId() + "释放锁：" + System.currentTimeMillis());
            }
        }
    }
}
