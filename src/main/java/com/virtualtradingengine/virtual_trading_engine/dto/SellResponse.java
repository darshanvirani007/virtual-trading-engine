package com.virtualtradingengine.virtual_trading_engine.dto;

import java.math.BigDecimal;

public record SellResponse(
        String symbol,
        int quantitySold,
        BigDecimal sellPrice,
        BigDecimal proceeds,
        BigDecimal averageBuyPrice,
        BigDecimal realizedProfitLoss
) {}