package com.kaiser.messenger_server.configuration;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import java.io.File;
import java.util.List;
import java.util.function.Predicate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.kaiser.messenger_server.modules.auth.BlacklistTokenRepository;
import com.kaiser.messenger_server.modules.auth.entity.BlacklistToken;
import com.kaiser.messenger_server.modules.track.TrackRepository;
import com.kaiser.messenger_server.modules.user.UserRepository;

@Slf4j
@Component
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CleanupRunner{
    BlacklistTokenRepository blacklistTokenRepository;
    UserRepository userRepository;
    TrackRepository trackRepository;

    @Scheduled(cron = "0 */5 * * * ?")  
    public void cleanupExpiredTokens() {
        try {
            List<BlacklistToken> blacklistTokens = blacklistTokenRepository.findAll();
            if(blacklistTokens.size() > 0){
                blacklistTokenRepository.deleteTokensExpiredBefore();
                log.info("Scheduled cleanup: Deleted tokens expiring before");
            }
        } catch (Exception e) {
            log.error("Failed to clean up expired tokens", e);
        }
    }

    @Scheduled(cron = "0 */5 * * * ?")
    public void cleanUpFiles() {
        try {
            cleanOrphanFiles("public/images/user", (filename) -> userRepository.existsByImage(filename));

            cleanOrphanFiles("public/images/track", (filename) -> trackRepository.existsByArtwork(filename));

            cleanOrphanFiles("public/tracks/song", (filename) -> {
                String url = "http://localhost:3000/api/v1/tracks/song/" + filename;
                return trackRepository.existsByUrl(url);
            });
        } catch (Exception e) {
            log.error("Error during cleanup of files", e);
        }
    }

    private void cleanOrphanFiles(String path, Predicate<String> existsInDb) {
        File folder = new File(path);

        if (!folder.exists() || !folder.isDirectory()) {
            log.warn("Directory not found: {}", path);
            return;
        }

        File[] files = folder.listFiles();

        if (files == null) return;

        List<String> fileList = List.of("unknown_track.png", "default-1752056150533.png");

        for (File file : files) {
            if (file.isFile()) {
                String filename = file.getName();
                boolean exists = existsInDb.test(filename);
                boolean found = fileList.contains(filename);

                if(found) continue;

                if (!exists) {
                    boolean deleted = file.delete();
                    if (deleted) {
                        log.info("Deleted orphan file: {}/{}", path, filename);
                    } else {
                        log.warn("Failed to delete orphan file: {}/{}", path, filename);
                    }
                }
            }
        }
    }
}
