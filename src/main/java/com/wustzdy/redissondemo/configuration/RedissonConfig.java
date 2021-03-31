package com.wustzdy.redissondemo.configuration;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

/**
 * redisson配置
 * 只需在使用的地方注入RedissonConfig即可
 *
 * @Autowired private RedissonClient redissonClient
 * <p>
 * Redisson内部提供了一个监控锁的看门狗，它的作用是在Redisson实例被关闭前，不断的延长锁的有效期。
 * 默认情况下，看门狗的检查锁的超时时间是30秒钟，也可以通过修改Config.lockWatchdogTimeout
 * @Author zdy
 * @Date 2020/9/20 16:31
 * @Param
 * @return
 **/
@Configuration
public class RedissonConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private String port;

    @Value("${spring.redis.password}")
    private String password;

    /**
     * redisson配置
     *
     * @return
     * @Author zdy
     * @Date 2020/9/20 16:31
     * @Param
     **/
    @Bean
    public RedissonClient redissonClient() {

        Config config = new Config();

        // 使用单节点服务
        config.useSingleServer().setAddress("redis://" + host + ":" + port);

        // 如果密码为空
        config.useSingleServer().setPassword(StringUtils.isEmpty(password) ? null : password);

        // 设置看门狗检查锁的超时时间
        /*config.setLockWatchdogTimeout(1000 * 60 * 5);*/

        // 主从配置  设置主机地址，密码 设置从机地址
        /*config.useMasterSlaveServers().setMasterAddress("")
                .setPassword("").addSlaveAddress(new String[]{"", ""});*/

        // 集群模式配置 setScanInterval(2000) 设置扫描间隔时间
        /*config.useClusterServers().setScanInterval(2000)
                .addNodeAddress("redis://127.0.0.1:7000", "redis://127.0.0.1:7001")
                .addNodeAddress("redis://127.0.0.1:7002");*/

        return Redisson.create(config);
    }
}
