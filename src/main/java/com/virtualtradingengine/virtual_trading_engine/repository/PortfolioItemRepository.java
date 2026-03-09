package com.virtualtradingengine.virtual_trading_engine.repository;

import com.virtualtradingengine.virtual_trading_engine.entity.PortfolioItem;
import com.virtualtradingengine.virtual_trading_engine.entity.Stock;
import com.virtualtradingengine.virtual_trading_engine.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface PortfolioItemRepository extends JpaRepository<PortfolioItem, Long> {

    Optional<PortfolioItem> findByUserAndStock(User user, Stock stock);
    List<PortfolioItem> findByUser(User user);
}