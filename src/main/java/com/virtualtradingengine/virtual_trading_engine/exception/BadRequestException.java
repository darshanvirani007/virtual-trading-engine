package com.virtualtradingengine.virtual_trading_engine.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}