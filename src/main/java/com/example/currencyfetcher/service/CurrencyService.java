package com.example.currencyfetcher.service.impl;

import com.example.currencyfetcher.cache.CachedCurrency;
import com.example.currencyfetcher.model.CurrencyRate;
import com.example.currencyfetcher.repository.CurrencyRateRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class CurrencyService {

    @Value("${exchange.api.key}")
    private String apiKey;

    private final CurrencyRateRepository repository;
    private final WebClient webClient;

    // In-memory cache: currency code → rate + timestamp
    private Map<String, CachedCurrency> cache = new ConcurrentHashMap<>();

    public CurrencyService(CurrencyRateRepository repository, @Value("${exchange.api.key}") String apiKey) {
        this.repository = repository;
        this.apiKey = apiKey;
        this.webClient = WebClient.builder()
                .baseUrl("https://api.apilayer.com/exchangerates_data")
                .defaultHeader("apikey", apiKey)
                .build();
    }

    /**
     * Phase 2: Scheduled task to fetch exchange rates from external API every 60 seconds.
     * Stores all fetched rates into DB with current timestamp.
     */
    @Scheduled(fixedRate = 60000) // every 60 seconds
    public void fetchAndSaveRates() {
        String url = "/latest?access_key=" + apiKey;

        System.out.println("Scheduling has started."); // Troubleshooting

        webClient.get()
                .uri(url)
                .retrieve()
                .bodyToMono(Map.class)
                .subscribe(response -> {
                    Map<String, Object> rates = (Map<String, Object>) response.get("rates");

                    if (rates == null) {
                        System.err.println("API response does not contain rates.");
                        return;
                    }

                    LocalDateTime now = LocalDateTime.now();

                    rates.forEach((currency, rateObj) -> {
                        try {
                            Double rate = ((Number) rateObj).doubleValue();
                            CurrencyRate cr = new CurrencyRate(null, currency, rate, now);
                            repository.save(cr);
                        } catch (Exception e) {
                            System.err.println("Failed to parse rate for " + currency + ": " + e.getMessage());
                        }
                    });
                }, error -> {
                    System.err.println("Failed to fetch exchange rates: " + error.getMessage());
                });
    }

    /**
     * Tries to retrieve the latest rate from cache first (if fresh).
     * If cache is missing or expired, falls back to DB and re-caches it.
     */
    public Optional<CachedCurrency> getCurrencyRate(String currency) {
        String key = currency.toUpperCase();
        CachedCurrency cached = cache.get(key);
        LocalDateTime now = LocalDateTime.now();

        if (cached != null) {
            long secondsElapsed = Duration.between(cached.getTimestamp(), now).getSeconds();
            if (secondsElapsed < 60) {
                return Optional.of(cached); // Use cache
            }
        }

        //Cache miss or expired
        CurrencyRate latest = repository.findTopByCurrencyCodeOrderByTimestampDesc(key);
        if (latest != null) {
            CachedCurrency newCache = new CachedCurrency(latest.getRate(), latest.getTimestamp());
            cache.put(key, newCache);
            return Optional.of(newCache);
        }

        return Optional.empty();
    }
}

