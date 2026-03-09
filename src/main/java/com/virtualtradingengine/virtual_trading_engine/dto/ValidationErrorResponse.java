package com.virtualtradingengine.virtual_trading_engine.dto;

import java.time.Instant;
import java.util.Map;

public record ValidationErrorResponse(
        String error,
        String message,
        Instant timestamp,
        Map<String, String> details
) {}