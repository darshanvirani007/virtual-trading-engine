package com.virtualtradingengine.virtual_trading_engine.controller;

import com.virtualtradingengine.virtual_trading_engine.dto.StockQuoteResponse;
import com.virtualtradingengine.virtual_trading_engine.service.StockPriceService;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@RequestMapping("/api/v1/stocks")
public class StockController {

    private final StockPriceService stockPriceService;

    public StockController(StockPriceService stockPriceService) {
        this.stockPriceService = stockPriceService;
    }
    @Operation(summary = "Fetch live stock quote from market data provider")
    @GetMapping("/{symbol}/quote")
    public StockQuoteResponse getQuote(@PathVariable String symbol) {
        return stockPriceService.getQuote(symbol.toUpperCase());
    }
}