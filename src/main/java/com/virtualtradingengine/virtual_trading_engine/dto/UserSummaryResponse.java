package com.virtualtradingengine.virtual_trading_engine.dto;

import java.math.BigDecimal;

public record UserSummaryResponse(
        Long userId,
        BigDecimal cashBalance,
        BigDecimal totalMarketValue,
        BigDecimal totalEquity,
        BigDecimal totalUnrealizedProfitLoss,
        BigDecimal totalUnrealizedProfitLossPercent
) {}