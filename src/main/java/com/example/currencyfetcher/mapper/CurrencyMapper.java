package com.example.currencyfetcher.mapper;

import com.example.currencyfetcher.dto.CurrencyRateHistoryDto;
import com.example.currencyfetcher.dto.CurrencyResponseDto;
import com.example.currencyfetcher.model.CurrencyRate;

public final class CurrencyMapper {

    private CurrencyMapper() {}

    public static CurrencyResponseDto toDto(CurrencyRate entity) {
        return new CurrencyResponseDto(
                entity.getId().getCurrencyCode(),
                entity.getRate(),
                entity.getId().getTimestamp()
        );
    }

    public static CurrencyRateHistoryDto toHistoryDto(CurrencyRate entity) {
        return new CurrencyRateHistoryDto(
                entity.getId().getCurrencyCode(),
                entity.getRate(),
                entity.getId().getTimestamp()
        );
    }
}
