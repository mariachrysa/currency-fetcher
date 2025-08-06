package com.example.currencyfetcher.mapper;

import com.example.currencyfetcher.dto.ConvertedCurrencyDto;
import com.example.currencyfetcher.dto.CurrencyResponseDto;

import java.math.BigDecimal;

public final class ConvertedCurrencyMapper {

    private ConvertedCurrencyMapper() {}

    public static ConvertedCurrencyDto toDto(CurrencyResponseDto from,
                                             CurrencyResponseDto to,
                                             BigDecimal amount,
                                             BigDecimal convertedAmount) {
        return new ConvertedCurrencyDto(
                from.currencyCode(),
                to.currencyCode(),
                amount,
                convertedAmount
        );
    }
}
