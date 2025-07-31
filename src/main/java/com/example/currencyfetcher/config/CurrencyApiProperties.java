package com.example.currencyfetcher.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "currency.api")
public class CurrencyApiProperties {
    private String key;
    private String baseUrl;
}

