package com.virtualtradingengine.virtual_trading_engine.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
        String error,
        String message,
        Instant timestamp,
        Map<String, String> details
) {
    public ErrorResponse(String error, String message, Instant timestamp) {
        this(error, message, timestamp, null);
    }
}