package com.example.currencyfetcher.exceptions;

public class InvalidCurrencyException extends RuntimeException {
    public InvalidCurrencyException(String code) {
        super("Invalid currency code: " + code);
    }
}

