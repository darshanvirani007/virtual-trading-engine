package com.virtualtradingengine.virtual_trading_engine.dto.alphavantage;

import com.fasterxml.jackson.annotation.JsonProperty;

public record AlphaVantageGlobalQuoteResponse(
        @JsonProperty("Global Quote")
        AlphaVantageQuote globalQuote
) {}