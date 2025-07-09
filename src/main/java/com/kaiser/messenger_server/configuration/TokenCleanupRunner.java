package com.kaiser.messenger_server.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.kaiser.messenger_server.repositories.BlacklistTokenRepository;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenCleanupRunner{
    private final BlacklistTokenRepository blacklistTokenRepository;

    @Scheduled(cron = "0 */5 * * * ?")  
    public void cleanupExpiredTokens() {
        try {
            blacklistTokenRepository.deleteTokensExpiredBefore();
            log.info("Scheduled cleanup: Deleted tokens expiring before");
        } catch (Exception e) {
            log.error("Failed to clean up expired tokens", e);
        }
    }
}
