package com.example.currencyfetcher.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class CurrencyApiWebClientConfig {

    @Bean
    public WebClient currencyApiWebClient() {
        return WebClient.builder()
                .build(); // baseUrl is dynamic from properties
    }
}
