package com.example.currencyfetcher.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "DTO containing the latest currency rate and timestamp")
public record CurrencyResponseDto(
        @Schema(description = "3-letter currency code", example = "EUR")
        String currencyCode,

        @Schema(description = "Exchange rate", example = "1.0675")
        BigDecimal rate,

        @Schema(description = "Timestamp when the rate was fetched")
        LocalDateTime timestamp
) {}
