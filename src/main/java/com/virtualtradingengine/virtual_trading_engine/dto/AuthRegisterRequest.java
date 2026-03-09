package com.virtualtradingengine.virtual_trading_engine.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record AuthRegisterRequest(
        @NotBlank String username,
        @Email @NotBlank String email,
        @NotBlank String password,
        @NotNull @Min(0) BigDecimal startingBalance
) {}