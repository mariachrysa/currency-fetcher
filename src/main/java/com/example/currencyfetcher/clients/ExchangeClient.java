package com.example.currencyfetcher.clients;

import com.example.currencyfetcher.config.CurrencyApiProperties;
import com.example.currencyfetcher.dto.CurrencyApiResponseDto;
import com.example.currencyfetcher.exceptions.ExternalServiceException;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExchangeClient {

    private final WebClient webClient;
    private final CurrencyApiProperties apiProperties;

    @Retry(name = "exchangeClient", fallbackMethod = "fallbackResponse")
    public CurrencyApiResponseDto fetchLatestRates(String baseCurrency) {
        String uri = UriComponentsBuilder
                .fromUriString(apiProperties.getBaseUrl())
                .pathSegment(apiProperties.getKey(), "latest", baseCurrency)
                .build()
                .toUriString();

        log.info("Calling Exchange API: {}", uri);

        return webClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        response -> response.bodyToMono(String.class)
                                .defaultIfEmpty("No error body")
                                .flatMap(body -> {
                                    log.error("Exchange API error {}: {}", response.statusCode(), body);
                                    return Mono.error(new ExternalServiceException("Exchange API failed: " + body));
                                })
                )
                .bodyToMono(CurrencyApiResponseDto.class)
                .timeout(Duration.ofSeconds(5))
                .doOnError(ex -> log.error("Exchange API call failed", ex))
                .block();
    }

    // Fallback for Resilience4j Retry
    public CurrencyApiResponseDto fallbackResponse(String baseCurrency, Throwable ex) {
        log.error("Fallback triggered for fetchLatestRates with baseCurrency {}: {}", baseCurrency, ex.getMessage());
        throw new ExternalServiceException("Failed to fetch rates from external API after retries");
    }
}
