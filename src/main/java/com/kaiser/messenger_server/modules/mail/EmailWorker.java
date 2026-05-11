package com.kaiser.messenger_server.modules.mail;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailWorker {
    private final RedisTemplate<String, EmailJob> redisTemplate;
    private final EmailService emailService;

    @Scheduled(fixedDelay = 100) 
    public void processQueue() {
        EmailJob job = (EmailJob) redisTemplate.opsForList().leftPop("emailQueue");
        if (job != null) {
            try {
                emailService.sendTemplateEmail(job.getEmail(), job.getActivationCode(), job.getSubject());
            } catch (Exception e) {
                
            }
        }
    }
}