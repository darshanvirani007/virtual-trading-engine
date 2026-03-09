package com.virtualtradingengine.virtual_trading_engine.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "stock-api.alpha-vantage")
public record AlphaVantageProperties(
        String baseUrl,
        String apiKey
) {}