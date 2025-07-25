package com.example.currencyfetcher.clients;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Component
public class ExchangeClient {

    private final WebClient webClient;
    private final String apiKey;

    public ExchangeClient(WebClient webClient, @Value("${exchange.api.key}") String apiKey) {
        this.webClient = webClient;
        this.apiKey = apiKey;
    }

    public Mono<Map> fetchLatestRates(String baseCurrency) {
        String uri = String.format("/%s/latest/%s", apiKey, baseCurrency);
        return webClient.get()
                .uri(uri)
                .retrieve()
                .bodyToMono(Map.class);
    }
}
