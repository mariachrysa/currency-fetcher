package com.example.currencyfetcher.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.math.BigDecimal;
import java.util.Map;

public record CurrencyApiResponse(

        @JsonProperty("base_code")
        String baseCode,

        @JsonProperty("conversion_rates")
        Map<String, BigDecimal> conversionRates

) {
}

