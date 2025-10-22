package com.kaiser.messenger_server.modules.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.kaiser.messenger_server.modules.auth.entity.BlacklistToken;

@Repository
public interface BlacklistTokenRepository extends JpaRepository<BlacklistToken, String> {
    @Modifying(clearAutomatically = true)
    @Transactional
    @Query(value = "DELETE FROM BlacklistToken t WHERE t.expiryTime < CURRENT_TIMESTAMP", nativeQuery = true)
    void deleteTokensExpiredBefore();
}