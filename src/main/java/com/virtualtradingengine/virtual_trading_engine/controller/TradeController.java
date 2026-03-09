package com.virtualtradingengine.virtual_trading_engine.controller;

import com.virtualtradingengine.virtual_trading_engine.dto.*;
import com.virtualtradingengine.virtual_trading_engine.service.TradeService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/trades")
public class TradeController {

    private final TradeService tradeService;

    public TradeController(TradeService tradeService) {
        this.tradeService = tradeService;
    }

    @Operation(summary = "Buy stock (paper trading)")
    @PostMapping("/buy")
    public void buy(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody BuyRequest request
    ) {
        tradeService.buy(user.getUsername(), request.symbol(), request.quantity());
    }

    @Operation(summary = "Sell stock (paper trading)")
    @PostMapping("/sell")
    public SellResponse sell(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody SellRequest request
    ) {
        return tradeService.sell(user.getUsername(), request.symbol(), request.quantity());
    }

    @Operation(summary = "Get transaction history (paginated) for logged-in user")
    @GetMapping("/history")
    public PageResponse<TransactionResponse> history(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        return tradeService.getTransactions(user.getUsername(), page, size);
    }
}