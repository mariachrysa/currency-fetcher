package com.example.currencyfetcher.service;

import com.example.currencyfetcher.cache.CachedCurrency;
import com.example.currencyfetcher.dto.ConvertedCurrencyDto;
import com.example.currencyfetcher.dto.CurrencyResponseDto;
import com.example.currencyfetcher.model.CurrencyRate;

import java.util.List;
import java.util.Optional;

public interface CurrencyService {
    Optional<CurrencyResponseDto> getCachedCurrency(String currency);
    Optional<ConvertedCurrencyDto> convertCurrency(String from, String to, double amount);
    List<CurrencyResponseDto> filterByMinRate(double minRate);
    List<CurrencyResponseDto> getTopCurrencies(int limit);
    void fetchAndSaveRates(); // Scheduler
    Optional<CurrencyRate> getLatestFromDB(String code);
    Optional<CachedCurrency> getCurrencyRate(String code);
    CurrencyRate getLatestFromDBOrThrow(String code);
    CachedCurrency getCachedCurrencyOrThrow(String code);
    ConvertedCurrencyDto convertCurrencyOrThrow(String from, String to, double amount);
}
