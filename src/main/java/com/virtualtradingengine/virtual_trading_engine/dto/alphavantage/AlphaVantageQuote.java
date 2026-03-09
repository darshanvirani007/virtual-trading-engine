package com.virtualtradingengine.virtual_trading_engine.dto.alphavantage;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AlphaVantageQuote(
        @JsonProperty("01. symbol") String symbol,
        @JsonProperty("05. price") String price
) {}