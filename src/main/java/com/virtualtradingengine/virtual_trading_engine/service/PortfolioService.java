package com.virtualtradingengine.virtual_trading_engine.service;

import com.virtualtradingengine.virtual_trading_engine.dto.*;
import com.virtualtradingengine.virtual_trading_engine.entity.PortfolioItem;
import com.virtualtradingengine.virtual_trading_engine.entity.User;
import com.virtualtradingengine.virtual_trading_engine.repository.PortfolioItemRepository;
import com.virtualtradingengine.virtual_trading_engine.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class PortfolioService {

    private final UserRepository userRepository;
    private final PortfolioItemRepository portfolioItemRepository;
    private final StockPriceService stockPriceService;

    public PortfolioService(UserRepository userRepository,
                            PortfolioItemRepository portfolioItemRepository,
                            StockPriceService stockPriceService) {
        this.userRepository = userRepository;
        this.portfolioItemRepository = portfolioItemRepository;
        this.stockPriceService = stockPriceService;
    }

    @Transactional(readOnly = true)
    public PortfolioResponse getPortfolio(String username) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + username));

        List<PortfolioItem> holdings = portfolioItemRepository.findByUser(user);

        List<PortfolioItemResponse> items = holdings.stream().map(item -> {
            String symbol = item.getStock().getSymbol();

            BigDecimal lastPrice = stockPriceService.getQuote(symbol).price();
            item.getStock().setCurrentPrice(lastPrice);

            BigDecimal avgPrice = item.getAveragePrice();
            int qty = item.getQuantity();

            BigDecimal marketValue = lastPrice.multiply(BigDecimal.valueOf(qty));
            BigDecimal unrealized = lastPrice.subtract(avgPrice).multiply(BigDecimal.valueOf(qty));

            BigDecimal unrealizedPercent;
            if (avgPrice.compareTo(BigDecimal.ZERO) == 0) {
                unrealizedPercent = BigDecimal.ZERO;
            } else {
                unrealizedPercent = lastPrice.subtract(avgPrice)
                        .divide(avgPrice, 6, RoundingMode.HALF_UP)
                        .multiply(BigDecimal.valueOf(100));
            }

            return new PortfolioItemResponse(
                    symbol,
                    qty,
                    avgPrice,
                    lastPrice,
                    marketValue,
                    unrealized,
                    unrealizedPercent
            );
        }).toList();

        BigDecimal totalMarketValue = items.stream()
                .map(PortfolioItemResponse::marketValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalUnrealized = items.stream()
                .map(PortfolioItemResponse::unrealizedProfitLoss)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalEquity = user.getBalance().add(totalMarketValue);

        BigDecimal totalCostBasis = holdings.stream()
                .map(h -> h.getAveragePrice().multiply(BigDecimal.valueOf(h.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalUnrealizedPercent;
        if (totalCostBasis.compareTo(BigDecimal.ZERO) == 0) {
            totalUnrealizedPercent = BigDecimal.ZERO;
        } else {
            totalUnrealizedPercent = totalUnrealized
                    .divide(totalCostBasis, 6, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100));
        }

        return new PortfolioResponse(
                user.getId(),
                user.getBalance(),
                totalMarketValue,
                totalEquity,
                totalUnrealized,
                totalUnrealizedPercent,
                items
        );
    }

    @Transactional(readOnly = true)
    public UserSummaryResponse getUserSummary(String username) {
        PortfolioResponse portfolio = getPortfolio(username);
        return new UserSummaryResponse(
                portfolio.userId(),
                portfolio.cashBalance(),
                portfolio.totalMarketValue(),
                portfolio.totalEquity(),
                portfolio.totalUnrealizedProfitLoss(),
                portfolio.totalUnrealizedProfitLossPercent()
        );
    }
}