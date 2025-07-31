package com.example.currencyfetcher.cache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@RequiredArgsConstructor
public class CacheService {

    private static final int TTL_SECONDS = 60;

    private final ConcurrentHashMap<String, CachedCurrency> cache = new ConcurrentHashMap<>();

    public Optional<CachedCurrency> getIfFresh(String code) {
        CachedCurrency cached = cache.get(code.toUpperCase());
        if (cached != null && !isExpired(cached.getTimestamp())) {
            return Optional.of(cached);
        }
        return Optional.empty();
    }

    public CachedCurrency update(String code, BigDecimal rate, LocalDateTime timestamp) {
        if (rate == null || timestamp == null) {
            throw new IllegalArgumentException("Cannot cache null values.");
        }
        CachedCurrency updated = new CachedCurrency(rate, timestamp);
        cache.put(code.toUpperCase(), updated);
        return updated;
    }


    private boolean isExpired(LocalDateTime timestamp) {
        return Duration.between(timestamp, LocalDateTime.now()).getSeconds() > TTL_SECONDS;
    }

    public void clear() {
        cache.clear();
    }
}
