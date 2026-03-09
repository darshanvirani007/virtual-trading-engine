package com.virtualtradingengine.virtual_trading_engine.repository;

import com.virtualtradingengine.virtual_trading_engine.entity.RefreshToken;
import com.virtualtradingengine.virtual_trading_engine.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    void deleteAllByUser(User user);

    @Modifying
    @Query("""
        update RefreshToken rt
        set rt.revokedAt = :revokedAt
        where rt.tokenHash = :tokenHash and rt.revokedAt is null
    """)
    int revokeByTokenHash(@Param("tokenHash") String tokenHash,
                          @Param("revokedAt") LocalDateTime revokedAt);

    @Modifying
    @Query("""
        delete from RefreshToken rt
        where rt.expiresAt < :now
           or (rt.revokedAt is not null and rt.revokedAt < :cutoff)
    """)
    int deleteExpiredOrOldRevoked(@Param("now") LocalDateTime now,
                                  @Param("cutoff") LocalDateTime cutoff);
}