package com.wustzdy.redissondemo.lock;

import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LockService {
    @Autowired
    private RedissonClient redissonClient;

    public void TestLock() {

    }
}
