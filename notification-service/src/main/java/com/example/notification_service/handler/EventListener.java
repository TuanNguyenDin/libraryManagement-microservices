package com.example.notification_service.handler;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component
public class EventListener {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @PostConstruct
    public void subscribeToRedis() {
        redisTemplate.getConnectionFactory().getConnection()
                .subscribe((message, pattern) -> {
                    String event = new String(message.getBody(), StandardCharsets.UTF_8);
                    System.out.println("📩 Nhận sự kiện: " + event);

                    messagingTemplate.convertAndSend("/topic/notifications", event);
                }, "book-events".getBytes());
    }
}
