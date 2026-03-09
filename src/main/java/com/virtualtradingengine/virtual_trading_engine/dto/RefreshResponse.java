package com.virtualtradingengine.virtual_trading_engine.dto;

public record RefreshResponse(
        String accessToken,
        String refreshToken,
        String tokenType
) {}