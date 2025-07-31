package com.example.currencyfetcher.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Schema(description = "DTO representing historical exchange rates for a currency")
public record CurrencyRateHistoryDto(
        @Schema(description = "Currency code", example = "JPY")
        String currencyCode,

        @Schema(description = "Rate at that point in time", example = "148.50")
        BigDecimal rate,

        @Schema(description = "Timestamp of this rate")
        LocalDateTime timestamp
) {}

