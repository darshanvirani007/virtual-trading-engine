package com.virtualtradingengine.virtual_trading_engine.controller;

import com.virtualtradingengine.virtual_trading_engine.dto.PortfolioResponse;
import com.virtualtradingengine.virtual_trading_engine.dto.UserSummaryResponse;
import com.virtualtradingengine.virtual_trading_engine.service.PortfolioService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/portfolio")
public class PortfolioController {

    private final PortfolioService portfolioService;

    public PortfolioController(PortfolioService portfolioService) {
        this.portfolioService = portfolioService;
    }

    @Operation(summary = "Get portfolio holdings with unrealized P/L (logged-in user)")
    @GetMapping
    public PortfolioResponse getPortfolio(@AuthenticationPrincipal User user) {
        return portfolioService.getPortfolio(user.getUsername());
    }

    @Operation(summary = "Get portfolio summary for dashboard (logged-in user)")
    @GetMapping("/summary")
    public UserSummaryResponse summary(@AuthenticationPrincipal User user) {
        return portfolioService.getUserSummary(user.getUsername());
    }
}