package com.virtualtradingengine.virtual_trading_engine.service;

import com.virtualtradingengine.virtual_trading_engine.dto.PageResponse;
import com.virtualtradingengine.virtual_trading_engine.dto.SellResponse;
import com.virtualtradingengine.virtual_trading_engine.dto.TransactionResponse;
import com.virtualtradingengine.virtual_trading_engine.entity.*;
import com.virtualtradingengine.virtual_trading_engine.exception.BadRequestException;
import com.virtualtradingengine.virtual_trading_engine.exception.NotFoundException;
import com.virtualtradingengine.virtual_trading_engine.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class TradeService {

    private static final Logger log = LoggerFactory.getLogger(TradeService.class);

    private final UserRepository userRepository;
    private final StockRepository stockRepository;
    private final PortfolioItemRepository portfolioItemRepository;
    private final TransactionRepository transactionRepository;
    private final StockPriceService stockPriceService;

    public TradeService(UserRepository userRepository,
                        StockRepository stockRepository,
                        PortfolioItemRepository portfolioItemRepository,
                        TransactionRepository transactionRepository,
                        StockPriceService stockPriceService) {
        this.userRepository = userRepository;
        this.stockRepository = stockRepository;
        this.portfolioItemRepository = portfolioItemRepository;
        this.transactionRepository = transactionRepository;
        this.stockPriceService = stockPriceService;
    }

    @Transactional
    public void buy(String username, String symbol, int quantity) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        String normalizedSymbol = symbol.toUpperCase();

        BigDecimal livePrice = stockPriceService.getQuote(normalizedSymbol).price();
        BigDecimal totalCost = livePrice.multiply(BigDecimal.valueOf(quantity));

        if (user.getBalance().compareTo(totalCost) < 0) {
            throw new BadRequestException("Insufficient balance");
        }

        Stock stock = stockRepository.findBySymbol(normalizedSymbol)
                .orElseGet(() -> stockRepository.save(
                        Stock.builder()
                                .symbol(normalizedSymbol)
                                .name(normalizedSymbol)
                                .currentPrice(livePrice)
                                .build()
                ));

        stock.setCurrentPrice(livePrice);
        user.setBalance(user.getBalance().subtract(totalCost));

        PortfolioItem item = portfolioItemRepository.findByUserAndStock(user, stock).orElse(null);

        if (item == null) {
            item = PortfolioItem.builder()
                    .user(user)
                    .stock(stock)
                    .quantity(quantity)
                    .averagePrice(livePrice)
                    .build();
        } else {
            int oldQty = item.getQuantity();
            int newQty = oldQty + quantity;

            BigDecimal oldTotal = item.getAveragePrice().multiply(BigDecimal.valueOf(oldQty));
            BigDecimal newAvg = oldTotal.add(totalCost)
                    .divide(BigDecimal.valueOf(newQty), 6, RoundingMode.HALF_UP);

            item.setQuantity(newQty);
            item.setAveragePrice(newAvg);
        }

        portfolioItemRepository.save(item);

        Transaction tx = Transaction.builder()
                .user(user)
                .stock(stock)
                .quantity(quantity)
                .price(livePrice)
                .createdAt(LocalDateTime.now())
                .type(TransactionType.BUY)
                .build();

        transactionRepository.save(tx);

        log.info("BUY executed: user={} symbol={} qty={} price={} total={}",
                username, normalizedSymbol, quantity, livePrice, totalCost);
    }

    @Transactional
    public SellResponse sell(String username, String symbol, int quantity) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        String normalizedSymbol = symbol.toUpperCase();

        Stock stock = stockRepository.findBySymbol(normalizedSymbol)
                .orElseThrow(() -> new NotFoundException("Stock not found: " + normalizedSymbol));

        PortfolioItem item = portfolioItemRepository.findByUserAndStock(user, stock)
                .orElseThrow(() -> new NotFoundException("No holdings for: " + normalizedSymbol));

        if (item.getQuantity() < quantity) {
            throw new BadRequestException("Not enough shares to sell. Owned: " + item.getQuantity());
        }

        BigDecimal sellPrice = stockPriceService.getQuote(normalizedSymbol).price();
        BigDecimal proceeds = sellPrice.multiply(BigDecimal.valueOf(quantity));

        stock.setCurrentPrice(sellPrice);
        user.setBalance(user.getBalance().add(proceeds));

        BigDecimal avgBuy = item.getAveragePrice();
        BigDecimal realized = sellPrice.subtract(avgBuy)
                .multiply(BigDecimal.valueOf(quantity));

        int remaining = item.getQuantity() - quantity;
        if (remaining == 0) {
            portfolioItemRepository.delete(item);
        } else {
            item.setQuantity(remaining);
            portfolioItemRepository.save(item);
        }

        Transaction tx = Transaction.builder()
                .user(user)
                .stock(stock)
                .quantity(quantity)
                .price(sellPrice)
                .createdAt(LocalDateTime.now())
                .type(TransactionType.SELL)
                .build();

        transactionRepository.save(tx);

        log.info("SELL executed: user={} symbol={} qty={} price={} proceeds={} realizedPnL={}",
                username, normalizedSymbol, quantity, sellPrice, proceeds, realized);

        return new SellResponse(
                normalizedSymbol,
                quantity,
                sellPrice,
                proceeds,
                avgBuy,
                realized
        );
    }

    @Transactional(readOnly = true)
    public PageResponse<TransactionResponse> getTransactions(String username, int page, int size) {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found: " + username));

        PageRequest pageable = PageRequest.of(
                page,
                size,
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        Page<Transaction> p = transactionRepository.findByUser(user, pageable);

        List<TransactionResponse> items = p.stream()
                .map(tx -> new TransactionResponse(
                        tx.getType().name(),
                        tx.getStock().getSymbol(),
                        tx.getQuantity(),
                        tx.getPrice(),
                        tx.getCreatedAt()
                ))
                .toList();

        return new PageResponse<>(
                items,
                p.getNumber(),
                p.getSize(),
                p.getTotalElements(),
                p.getTotalPages(),
                p.isLast()
        );
    }
}