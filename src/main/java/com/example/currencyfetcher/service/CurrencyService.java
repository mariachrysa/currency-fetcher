package com.example.currencyfetcher.service;

import com.example.currencyfetcher.dto.ConvertedCurrencyDto;
import com.example.currencyfetcher.dto.CurrencyRateHistoryDto;
import com.example.currencyfetcher.dto.CurrencyResponseDto;
import com.example.currencyfetcher.model.CurrencyRate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CurrencyService {
    Optional<CurrencyResponseDto> getCachedCurrency(String currency);
    ConvertedCurrencyDto convert(String from, String to, BigDecimal amount);
    List<CurrencyResponseDto> filterByMinRate(double minRate);
    List<CurrencyResponseDto> getTopCurrencies(int limit);
    Optional<CurrencyRate> getLatestFromDB(String code);
    void fetchAndSaveRates(); // Scheduled method
    CurrencyResponseDto getRatesForCurrency(String code);
    List<CurrencyRateHistoryDto> getHistoryForCurrency(String code);
}
