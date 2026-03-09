package com.virtualtradingengine.virtual_trading_engine.security;

import com.virtualtradingengine.virtual_trading_engine.repository.RefreshTokenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class RefreshTokenCleanupJob {

    private static final Logger log = LoggerFactory.getLogger(RefreshTokenCleanupJob.class);

    private final RefreshTokenRepository refreshTokenRepository;

    public RefreshTokenCleanupJob(RefreshTokenRepository refreshTokenRepository) {
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Scheduled(cron = "0 0 3 * * *") // every day at 03:00
    @Transactional
    public void cleanup() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime revokedCutoff = now.minusDays(7);

        int deleted = refreshTokenRepository.deleteExpiredOrOldRevoked(now, revokedCutoff);

        log.info("Refresh token cleanup finished: deleted={}", deleted);
    }
}