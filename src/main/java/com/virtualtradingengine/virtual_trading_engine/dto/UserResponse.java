package com.virtualtradingengine.virtual_trading_engine.dto;

import java.math.BigDecimal;

public record UserResponse(
        Long id,
        String username,
        String email,
        BigDecimal Balance
) {}