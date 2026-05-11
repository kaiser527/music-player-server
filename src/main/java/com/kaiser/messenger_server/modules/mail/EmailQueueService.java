package com.kaiser.messenger_server.modules.mail;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailQueueService {
    private final RedisTemplate<String, EmailJob> redisTemplate;

    public void enqueueEmail(String email, String code, String subject) {
        EmailJob job = new EmailJob(email, code, subject);
        redisTemplate.opsForList().rightPush("emailQueue", job);
    }
}