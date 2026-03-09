package com.virtualtradingengine.virtual_trading_engine.security;

import com.virtualtradingengine.virtual_trading_engine.entity.RefreshToken;
import com.virtualtradingengine.virtual_trading_engine.entity.User;
import com.virtualtradingengine.virtual_trading_engine.exception.BadRequestException;
import com.virtualtradingengine.virtual_trading_engine.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final long refreshExpirationMs;
    private final SecureRandom secureRandom = new SecureRandom();

    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               @Value("${security.refresh.expiration}") long refreshExpirationMs) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshExpirationMs = refreshExpirationMs;
    }

    public String issueRefreshToken(User user) {
        String rawToken = generateTokenValue();
        String hash = sha256Hex(rawToken);

        RefreshToken entity = RefreshToken.builder()
                .user(user)
                .tokenHash(hash)
                .createdAt(LocalDateTime.now())
                .expiresAt(LocalDateTime.now().plusSeconds(refreshExpirationMs / 1000))
                .revokedAt(null)
                .build();

        refreshTokenRepository.save(entity);
        return rawToken;
    }

    /**
     * Validate old token, revoke it, and return the username for reloading the user safely.
     */
    @Transactional
    public String validateAndRevoke(String rawRefreshToken) {
        String hash = sha256Hex(rawRefreshToken);

        RefreshToken token = refreshTokenRepository.findByTokenHash(hash)
                .orElseThrow(() -> new BadRequestException("Invalid refresh token"));

        if (token.getRevokedAt() != null) {
            throw new BadRequestException("Refresh token revoked");
        }

        if (token.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new BadRequestException("Refresh token expired");
        }

        token.setRevokedAt(LocalDateTime.now());
        refreshTokenRepository.save(token);

        // Access username inside the transaction while lazy relation is available
        return token.getUser().getUsername();
    }

    @Transactional
    public void revoke(String rawRefreshToken) {
        String hash = sha256Hex(rawRefreshToken);
        int updated = refreshTokenRepository.revokeByTokenHash(hash, LocalDateTime.now());
        if (updated == 0) {
            throw new BadRequestException("Invalid refresh token");
        }
    }

    private String generateTokenValue() {
        byte[] bytes = new byte[32];
        secureRandom.nextBytes(bytes);
        return java.util.Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static String sha256Hex(String value) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashed = digest.digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hashed) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 not available", e);
        }
    }
}