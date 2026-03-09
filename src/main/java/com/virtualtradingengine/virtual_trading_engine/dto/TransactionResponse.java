package com.virtualtradingengine.virtual_trading_engine.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransactionResponse(
        String type,
        String symbol,
        int quantity,
        BigDecimal price,
        LocalDateTime timestamp
) {}