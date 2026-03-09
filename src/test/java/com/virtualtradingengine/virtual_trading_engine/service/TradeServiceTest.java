package com.virtualtradingengine.virtual_trading_engine.service;
import com.virtualtradingengine.virtual_trading_engine.dto.StockQuoteResponse;
import com.virtualtradingengine.virtual_trading_engine.entity.Role;
import com.virtualtradingengine.virtual_trading_engine.entity.User;
import com.virtualtradingengine.virtual_trading_engine.exception.BadRequestException;
import com.virtualtradingengine.virtual_trading_engine.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TradeServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private StockRepository stockRepository;

    @Mock
    private PortfolioItemRepository portfolioItemRepository;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private StockPriceService stockPriceService;

    private TradeService tradeService;

    @BeforeEach
    void setup() {
        tradeService = new TradeService(
                userRepository,
                stockRepository,
                portfolioItemRepository,
                transactionRepository,
                stockPriceService
        );
    }

    @Test
    void buy_shouldThrowException_whenBalanceIsInsufficient() {

        User user = User.builder()
                .id(1L)
                .username("darshan")
                .balance(BigDecimal.valueOf(100))
                .role(Role.USER)
                .build();

        when(userRepository.findByUsername("darshan"))
                .thenReturn(Optional.of(user));

        when(stockPriceService.getQuote("IBM"))
                .thenReturn(new com.virtualtradingengine.virtual_trading_engine.dto.StockQuoteResponse(
                        "IBM",
                        BigDecimal.valueOf(200)
                ));

        assertThrows(
                BadRequestException.class,
                () -> tradeService.buy("darshan", "IBM", 1)
        );
    }
}