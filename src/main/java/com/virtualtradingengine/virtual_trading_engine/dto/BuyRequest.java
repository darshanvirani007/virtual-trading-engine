package com.virtualtradingengine.virtual_trading_engine.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record BuyRequest(
        @NotBlank String symbol,
        @NotNull @Min(1) Integer quantity
) {}