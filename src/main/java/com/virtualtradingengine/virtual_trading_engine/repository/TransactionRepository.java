package com.virtualtradingengine.virtual_trading_engine.repository;

import com.virtualtradingengine.virtual_trading_engine.entity.Transaction;
import com.virtualtradingengine.virtual_trading_engine.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.List;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    Page<Transaction> findByUser(User user, Pageable pageable);
}