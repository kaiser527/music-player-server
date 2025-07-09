package com.kaiser.messenger_server.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import com.kaiser.messenger_server.entities.BlacklistToken;

@Repository
public interface BlacklistTokenRepository extends JpaRepository<BlacklistToken, String> {
    @Modifying
    @Transactional
    @Query("DELETE FROM BlacklistToken t WHERE t.expiryTime < CURRENT_TIMESTAMP")
    void deleteTokensExpiredBefore();
}