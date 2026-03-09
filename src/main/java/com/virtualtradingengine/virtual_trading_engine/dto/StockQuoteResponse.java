package com.virtualtradingengine.virtual_trading_engine.dto;

import java.math.BigDecimal;

public record StockQuoteResponse(
        String symbol,
        BigDecimal price
) {}