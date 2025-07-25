package com.example.currencyfetcher.cache;

import java.time.LocalDateTime;

public class CachedCurrency {
    private double rate;
    private LocalDateTime timestamp;

    public CachedCurrency(double rate, LocalDateTime timestamp) {
        this.rate = rate;
        this.timestamp = timestamp;
    }

    public double getRate() {
        return rate;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
