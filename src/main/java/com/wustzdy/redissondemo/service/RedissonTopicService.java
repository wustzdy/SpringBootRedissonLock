package com.wustzdy.redissondemo.service;

import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RLock;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.redisson.client.codec.StringCodec;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

//可以在项目启动过程中时刻运行着
@Service
@Slf4j
public class RedissonTopicService implements ApplicationRunner, Ordered {
    @Autowired
    private RedissonClient redissonClient;
    private static String position;
    private static ScheduledExecutorService scheduledExecutorService;
    private static int LOCK_RELEASE_TIME = 60;
    private static int PERIOD_TIME = 30;
    private boolean flag;
    @Autowired
    @Lazy
    private RedissonTopicService self;
    private static final ConcurrentHashMap<String, MessageListener<String>> LISTENER_MAP = new ConcurrentHashMap<>();


    //在项目跑的过程中，不断的执行我们自定义的一些逻辑
    @Override
    public void run(ApplicationArguments args) throws Exception {
        log.info("----在项目跑的过程中，不断的执行我们自定义的一些逻辑---order01");
        this.listenTopic();
    }

    @Override
    public int getOrder() {
        return 1;
    }

    //监听对象实例-话题里面的消息
    public void listenTopic() {
        //start thread
        startThread();

        listenEvent();
    }

    private void startThread() {
        scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        scheduledExecutorService.scheduleAtFixedRate(
                () -> {
                    try {
                        campaign();
                    } catch (Exception e) {
                        log.error("leader selection schedule error", e);
                        throw e;
                    }
                }, 0, PERIOD_TIME, TimeUnit.SECONDS);
    }

    private void campaign() {
        RLock rLock = redissonClient.getLock("listen_event_test");
        try {
            boolean lockStatus = rLock.tryLock((long) 30, (long) 60, TimeUnit.SECONDS);
            if (lockStatus) {
                System.out.println("上锁" + lockStatus);
            } else {
                System.out.println("释放锁" + lockStatus);
            }

        } catch (InterruptedException e) {
            log.error("campaign error", e);
            throw new RuntimeException(e);
        } finally {
            rLock.unlock();
        }

    }

    private void listenEvent() {
        TopicMsgListener topicMsgListener = new TopicMsgListener("china", self);

        MessageListener<String> msgListener = LISTENER_MAP.putIfAbsent("china", topicMsgListener);
        System.out.println("--------------->msgListener:" + msgListener);
        System.out.println("--------------->topicMsgListener:" + topicMsgListener);

        if (msgListener == null) {
            RTopic rTopic = redissonClient.getTopic("china", new StringCodec());
            rTopic.addListener(String.class, topicMsgListener);
        }
    }

    private static class TopicMsgListener implements MessageListener<String> {
        private String topic;
        private RedissonTopicService stringMsgListenerFactory;

        private TopicMsgListener(String topic, RedissonTopicService stringMsgListenerFactory) {
            this.topic = topic;
            this.stringMsgListenerFactory = stringMsgListenerFactory;
        }

        @Override
        public void onMessage(CharSequence channel, String msg) {
            System.out.println("99999999999msg:" + msg);
            System.out.println("77777777channel:" + channel);
            stringMsgListenerFactory.onMessage(topic, msg);
        }
    }

    public void onMessage(String topic, String msg) {
        // 待根据event的meta获取tenant信息
        try {
            //up todo
        } finally {
        }
    }

}
