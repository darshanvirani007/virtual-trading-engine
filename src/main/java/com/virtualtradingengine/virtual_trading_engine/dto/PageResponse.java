package com.virtualtradingengine.virtual_trading_engine.dto;

import java.util.List;

public record PageResponse<T>(
        List<T> items,
        int page,
        int size,
        long totalElements,
        int totalPages,
        boolean isLast
) {}