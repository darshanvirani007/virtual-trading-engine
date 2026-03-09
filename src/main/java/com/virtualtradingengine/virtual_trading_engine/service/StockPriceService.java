package com.virtualtradingengine.virtual_trading_engine.service;

import com.virtualtradingengine.virtual_trading_engine.config.AlphaVantageProperties;
import com.virtualtradingengine.virtual_trading_engine.dto.StockQuoteResponse;
import com.virtualtradingengine.virtual_trading_engine.dto.alphavantage.AlphaVantageGlobalQuoteResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.cache.annotation.Cacheable;
import java.math.BigDecimal;

@Service
public class StockPriceService {

    private final AlphaVantageProperties properties;
    private final RestClient restClient;

    public StockPriceService(AlphaVantageProperties properties) {
        this.properties = properties;
        this.restClient = RestClient.builder()
                .baseUrl(properties.baseUrl())
                .build();
    }
    @Cacheable(cacheNames = "quotes", key = "#symbol")
    public StockQuoteResponse getQuote(String symbol) {

        AlphaVantageGlobalQuoteResponse response = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/query")
                        .queryParam("function", "GLOBAL_QUOTE")
                        .queryParam("symbol", symbol)
                        .queryParam("apikey", properties.apiKey())
                        .build())
                .retrieve()
                .body(AlphaVantageGlobalQuoteResponse.class);

        if (response == null || response.globalQuote() == null || response.globalQuote().price() == null) {
            throw new com.virtualtradingengine.virtual_trading_engine.exception.QuoteNotFoundException(
                    "No quote returned for symbol: " + symbol
            );
        }

        return new StockQuoteResponse(
                response.globalQuote().symbol(),
                new BigDecimal(response.globalQuote().price())
        );
    }
}