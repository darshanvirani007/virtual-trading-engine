package com.virtualtradingengine.virtual_trading_engine.dto;

import java.math.BigDecimal;

public record PortfolioItemResponse(
        String symbol,
        int quantity,
        BigDecimal averagePrice,
        BigDecimal lastPrice,
        BigDecimal marketValue,
        BigDecimal unrealizedProfitLoss,
        BigDecimal unrealizedProfitLossPercent
) {}