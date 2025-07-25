package com.example.currencyfetcher.service.impl;

import com.example.currencyfetcher.cache.CachedCurrency;
import com.example.currencyfetcher.clients.ExchangeClient;
import com.example.currencyfetcher.dto.ConvertedCurrencyDto;
import com.example.currencyfetcher.dto.CurrencyResponseDto;
import com.example.currencyfetcher.model.CurrencyRate;
import com.example.currencyfetcher.repository.CurrencyRateRepository;
import com.example.currencyfetcher.service.CurrencyService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import com.example.currencyfetcher.exceptions.InvalidCurrencyException;

@Service
public class CurrencyServiceImpl implements CurrencyService {

    @Value("${exchange.api.key}")
    private String apiKey;

    private final CurrencyRateRepository repository;
    private final Map<String, CachedCurrency> cache = new ConcurrentHashMap<>();
    private final ExchangeClient exchangeClient;

    public CurrencyServiceImpl(CurrencyRateRepository repository,
                               @Value("${exchange.api.key}") String apiKey,
                               ExchangeClient exchangeClient) {
        this.repository = repository;
        this.apiKey = apiKey;
        this.exchangeClient = exchangeClient;
    }

    @Override
    @Scheduled(fixedRate = 60000)
    public void fetchAndSaveRates() {
        String baseCurrency = "USD";

        exchangeClient.fetchLatestRates(baseCurrency).subscribe(response -> {
            Map<String, Object> conversionRates = (Map<String, Object>) response.get("conversion_rates");
            if (conversionRates == null) return;

            LocalDateTime now = LocalDateTime.now();

            conversionRates.forEach((currency, rateObj) -> {
                try {
                    double rate = ((Number) rateObj).doubleValue();
                    CurrencyRate cr = new CurrencyRate(null, currency, rate, now);
                    repository.save(cr);
                } catch (Exception e) {
                    System.err.println("Failed to parse " + currency + ": " + e.getMessage());
                }
            });
        }, error -> System.err.println("API error: " + error.getMessage()));
    }


    @Override
    public Optional<CurrencyResponseDto> getCachedCurrency(String currency) {
        String key = currency.toUpperCase();
        CachedCurrency cached = cache.get(key);
        LocalDateTime now = LocalDateTime.now();

        if (cached != null && Duration.between(cached.getTimestamp(), now).getSeconds() < 60) {
            CurrencyResponseDto dto = new CurrencyResponseDto(key, cached.getRate(), cached.getTimestamp());
            return Optional.of(dto);
        }

        CurrencyRate latest = repository.findTopByCurrencyCodeOrderByTimestampDesc(key);
        if (latest != null) {
            CachedCurrency refreshed = new CachedCurrency(latest.getRate(), latest.getTimestamp());
            cache.put(key, refreshed);
            CurrencyResponseDto dto = new CurrencyResponseDto(key, refreshed.getRate(), refreshed.getTimestamp());
            return Optional.of(dto);
        }

        return Optional.empty();
    }

    @Override
    public Optional<ConvertedCurrencyDto> convertCurrency(String from, String to, double amount) {
        Optional<CurrencyResponseDto> fromOpt = getCachedCurrency(from);
        Optional<CurrencyResponseDto> toOpt = getCachedCurrency(to);

        if (fromOpt.isEmpty() || toOpt.isEmpty()) return Optional.empty();

        double converted = amount * (toOpt.get().getRate() / fromOpt.get().getRate());

        ConvertedCurrencyDto dto = new ConvertedCurrencyDto(
                from.toUpperCase(), to.toUpperCase(), amount, converted
        );
        return Optional.of(dto);
    }

    @Override
    public List<CurrencyResponseDto> filterByMinRate(double minRate) {
        List<CurrencyRate> all = repository.findAll();

        Map<String, CurrencyRate> latestPerCurrency = all.stream()
                .collect(Collectors.toMap(
                        CurrencyRate::getCurrencyCode,
                        cr -> cr,
                        (cr1, cr2) -> cr1.getTimestamp().isAfter(cr2.getTimestamp()) ? cr1 : cr2
                ));

        return latestPerCurrency.values().stream()
                .filter(rate -> rate.getRate() >= minRate)
                .map(rate -> new CurrencyResponseDto(rate.getCurrencyCode(), rate.getRate(), rate.getTimestamp()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CurrencyResponseDto> getTopCurrencies(int limit) {
        List<CurrencyRate> all = repository.findAll();

        Map<String, CurrencyRate> latestPerCurrency = all.stream()
                .collect(Collectors.toMap(
                        CurrencyRate::getCurrencyCode,
                        cr -> cr,
                        (cr1, cr2) -> cr1.getTimestamp().isAfter(cr2.getTimestamp()) ? cr1 : cr2
                ));

        return latestPerCurrency.values().stream()
                .sorted(Comparator.comparingDouble(CurrencyRate::getRate).reversed())
                .limit(limit)
                .map(rate -> new CurrencyResponseDto(rate.getCurrencyCode(), rate.getRate(), rate.getTimestamp()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CurrencyRate> getLatestFromDB(String code) {
        return Optional.ofNullable(repository.findTopByCurrencyCodeOrderByTimestampDesc(code.toUpperCase()));
    }

    @Override
    public Optional<CachedCurrency> getCurrencyRate(String code) {
        return Optional.ofNullable(cache.get(code.toUpperCase()));
    }

    @Override
    public CurrencyRate getLatestFromDBOrThrow(String code) {
        return Optional.ofNullable(
                repository.findTopByCurrencyCodeOrderByTimestampDesc(code.toUpperCase())
        ).orElseThrow(() -> new InvalidCurrencyException(code));
    }

    @Override
    public CachedCurrency getCachedCurrencyOrThrow(String code) {
        return Optional.ofNullable(cache.get(code.toUpperCase()))
                .orElseThrow(() -> new InvalidCurrencyException(code));
    }

    @Override
    public ConvertedCurrencyDto convertCurrencyOrThrow(String from, String to, double amount) {
        Optional<CurrencyResponseDto> fromOpt = getCachedCurrency(from);
        Optional<CurrencyResponseDto> toOpt = getCachedCurrency(to);

        if (fromOpt.isEmpty() || toOpt.isEmpty()) {
            throw new InvalidCurrencyException("One or both currency codes are invalid: " + from + ", " + to);
        }

        double converted = amount * (toOpt.get().getRate() / fromOpt.get().getRate());
        return new ConvertedCurrencyDto(from.toUpperCase(), to.toUpperCase(), amount, converted);
    }


}
