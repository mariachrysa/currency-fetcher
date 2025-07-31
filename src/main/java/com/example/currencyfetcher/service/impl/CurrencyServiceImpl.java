package com.example.currencyfetcher.service.impl;

import com.example.currencyfetcher.cache.CacheService;
import com.example.currencyfetcher.clients.ExchangeClient;
import com.example.currencyfetcher.dto.ConvertedCurrencyDto;
import com.example.currencyfetcher.dto.CurrencyApiResponseDto;
import com.example.currencyfetcher.dto.CurrencyRateHistoryDto;
import com.example.currencyfetcher.dto.CurrencyResponseDto;
import com.example.currencyfetcher.exceptions.InvalidCurrencyException;
import com.example.currencyfetcher.model.CurrencyRate;
import com.example.currencyfetcher.repository.CurrencyRateRepository;
import com.example.currencyfetcher.service.CurrencyService;
import com.example.currencyfetcher.validation.CurrencyValidator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CurrencyServiceImpl implements CurrencyService {

    private final ExchangeClient exchangeClient;
    private final CurrencyRateRepository repository;
    private final CacheService cacheService;
    private final CurrencyValidator validator;

    @Override
    public void fetchAndSaveRates() {
        try {
            CurrencyApiResponseDto response = exchangeClient.fetchLatestRates("USD");
            Map<String, BigDecimal> rates = response.conversionRates();

            if (rates == null || rates.isEmpty()) return;

            LocalDateTime now = LocalDateTime.now();

            List<CurrencyRate> rateEntities = rates.entrySet().stream()
                    .map(entry -> new CurrencyRate(
                            entry.getKey(),
                            entry.getValue(),
                            now
                    ))
                    .collect(Collectors.toList());

            repository.saveAll(rateEntities);

        } catch (Exception e) {
            log.error("fetch failed", e);
        }
    }

    @Override
    public Optional<CurrencyResponseDto> getCachedCurrency(String currency) {
        validator.validate(currency);

        return cacheService.getIfFresh(currency)
                .map(cached -> new CurrencyResponseDto(currency.toUpperCase(), cached.getRate(), cached.getTimestamp()))
                .or(() -> {
                    CurrencyRate latest = repository.findTopByIdCurrencyCodeOrderByIdTimestampDesc(currency.toUpperCase());
                    if (latest == null) return Optional.empty();
                    cacheService.update(currency, latest.getRate(), latest.getId().getTimestamp());
                    return Optional.of(new CurrencyResponseDto(currency.toUpperCase(), latest.getRate(), latest.getId().getTimestamp()));
                });
    }

    @Override
    public ConvertedCurrencyDto convert(String from, String to, BigDecimal amount) {
        validator.validate(from);
        validator.validate(to);

        CurrencyResponseDto fromRate = getCachedCurrency(from)
                .orElseThrow(() -> new InvalidCurrencyException(from));
        CurrencyResponseDto toRate = getCachedCurrency(to)
                .orElseThrow(() -> new InvalidCurrencyException(to));

        BigDecimal converted = amount
                .multiply(toRate.rate())
                .divide(fromRate.rate(), 4, RoundingMode.HALF_UP);

        return new ConvertedCurrencyDto(
                from.toUpperCase(),
                to.toUpperCase(),
                amount,
                converted
        );
    }

    @Override
    public List<CurrencyResponseDto> filterByMinRate(double minRate) {
        Map<String, CurrencyRate> latestRates = getLatestRatesMap(repository.findAll());

        return latestRates.values().stream()
                .filter(rate -> rate.getRate().compareTo(BigDecimal.valueOf(minRate)) >= 0)
                .map(rate -> new CurrencyResponseDto(rate.getId().getCurrencyCode(), rate.getRate(), rate.getId().getTimestamp()))
                .collect(Collectors.toList());
    }

    @Override
    public List<CurrencyResponseDto> getTopCurrencies(int limit) {
        Map<String, CurrencyRate> latestRates = getLatestRatesMap(repository.findAll());

        return latestRates.values().stream()
                .sorted(Comparator.comparing(CurrencyRate::getRate).reversed())
                .limit(limit)
                .map(rate -> new CurrencyResponseDto(rate.getId().getCurrencyCode(), rate.getRate(), rate.getId().getTimestamp()))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CurrencyRate> getLatestFromDB(String code) {
        validator.validate(code);
        return Optional.ofNullable(repository.findTopByIdCurrencyCodeOrderByIdTimestampDesc(code.toUpperCase()));
    }

    private Map<String, CurrencyRate> getLatestRatesMap(List<CurrencyRate> rates) {
        return rates.stream().collect(Collectors.toMap(
                rate -> rate.getId().getCurrencyCode(),
                r -> r,
                (r1, r2) -> r1.getId().getTimestamp().isAfter(r2.getId().getTimestamp()) ? r1 : r2
        ));
    }

    @Override
    public CurrencyResponseDto getRatesForCurrency(String code) {
        return getCachedCurrency(code)
                .map(dto -> new CurrencyResponseDto(dto.currencyCode(), dto.rate(), dto.timestamp()))
                .orElseThrow(() -> new InvalidCurrencyException(code));
    }

    @Override
    public List<CurrencyRateHistoryDto> getHistoryForCurrency(String code) {
        validator.validate(code);

        return repository.findByIdCurrencyCodeOrderByIdTimestampDesc(code.toUpperCase())
                .stream()
                .map(rate -> new CurrencyRateHistoryDto(
                        rate.getId().getCurrencyCode(),
                        rate.getRate(),
                        rate.getId().getTimestamp()
                ))
                .toList();
    }
}
