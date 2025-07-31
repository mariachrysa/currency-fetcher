package com.example.currencyfetcher.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;

@Schema(description = "DTO for currency conversion result")
public record ConvertedCurrencyDto(

        @Schema(description = "Currency code to convert from", example = "USD")
        String from,

        @Schema(description = "Currency code to convert to", example = "EUR")
        String to,

        @Schema(description = "Amount to convert", example = "100.00")
        BigDecimal amount,

        @Schema(description = "Converted amount", example = "92.50")
        BigDecimal convertedAmount
) {}
