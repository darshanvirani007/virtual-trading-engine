package com.virtualtradingengine.virtual_trading_engine.dto;

public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {}