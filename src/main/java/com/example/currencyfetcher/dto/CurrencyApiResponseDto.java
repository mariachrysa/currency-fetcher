package com.example.currencyfetcher.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.Map;

@Schema(description = "DTO for response from external exchange API")
public record CurrencyApiResponseDto(

        @JsonProperty("base_code")
        @Schema(description = "Base currency code", example = "USD")
        String baseCode,

        @JsonProperty("conversion_rates")
        @Schema(description = "Map of currency codes to exchange rates")
        Map<String, BigDecimal> conversionRates
) {}
