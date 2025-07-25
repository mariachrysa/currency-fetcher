package com.example.currencyfetcher.exceptions;

import com.example.currencyfetcher.repository.CurrencyRateRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    private final CurrencyRateRepository repository;

    public GlobalExceptionHandler(CurrencyRateRepository repository) {
        this.repository = repository;
    }

    @ExceptionHandler(InvalidCurrencyException.class)
    public ResponseEntity<?> handleInvalidCurrency(InvalidCurrencyException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "error", ex.getMessage(),
                "validCurrencies", repository.findAllCurrencies()
        ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Map.of(
                "error", "Unexpected server error",
                "details", ex.getMessage()
        ));
    }
}
