package com.virtualtradingengine.virtual_trading_engine.dto;

import java.math.BigDecimal;
import java.util.List;

public record PortfolioResponse(
        Long userId,
        BigDecimal cashBalance,
        BigDecimal totalMarketValue,
        BigDecimal totalEquity,
        BigDecimal totalUnrealizedProfitLoss,
        BigDecimal totalUnrealizedProfitLossPercent,
        List<PortfolioItemResponse> items
) {}