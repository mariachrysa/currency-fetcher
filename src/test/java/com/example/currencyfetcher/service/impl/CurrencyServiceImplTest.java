package com.example.currencyfetcher.service.impl;

import com.example.currencyfetcher.cache.CacheService;
import com.example.currencyfetcher.cache.CachedCurrency;
import com.example.currencyfetcher.clients.ExchangeClient;
import com.example.currencyfetcher.dto.CurrencyResponseDto;
import com.example.currencyfetcher.exceptions.ExternalServiceException;
import com.example.currencyfetcher.exceptions.InvalidCurrencyException;
import com.example.currencyfetcher.model.CurrencyRate;
import com.example.currencyfetcher.model.CurrencyRateId;
import com.example.currencyfetcher.repository.CurrencyRateRepository;
import com.example.currencyfetcher.validation.CurrencyValidator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CurrencyServiceImplTest {

    @Mock
    private ExchangeClient exchangeClient;

    @Mock
    private CurrencyRateRepository repository;

    @Mock
    private CacheService cacheService;

    @Mock
    private CurrencyValidator validator;

    @InjectMocks
    private CurrencyServiceImpl currencyService;

    @Test
    void shouldReturnCachedCurrency_whenCacheIsFresh() {
        // Arrange
        String currency = "EUR";
        BigDecimal rate = new BigDecimal("1.15");
        LocalDateTime timestamp = LocalDateTime.now();

        CachedCurrency cached = new CachedCurrency(currency, rate, timestamp);
        when(cacheService.getIfFresh(eq(currency))).thenReturn(Optional.of(cached));

        // Act
        Optional<CurrencyResponseDto> result = currencyService.getCachedCurrency(currency);

        // Assert
        assertThat(result).isPresent();
        CurrencyResponseDto dto = result.get();

        assertThat(dto.currencyCode()).isEqualTo("EUR");
        assertThat(dto.rate()).isEqualByComparingTo(rate);
        assertThat(dto.timestamp()).isEqualTo(timestamp);

        verify(cacheService).getIfFresh(eq(currency));
        verifyNoInteractions(repository); // Ensure DB was not called
    }

    @Test
    void shouldFetchFromDB_whenCacheIsEmpty() {
        // Arrange
        String currency = "GBP";
        BigDecimal rate = new BigDecimal("1.25");
        LocalDateTime timestamp = LocalDateTime.now();

        // Simulate missing cache
        when(cacheService.getIfFresh(eq(currency))).thenReturn(Optional.empty());

        // Simulate DB has latest value
        CurrencyRate fromDb = new CurrencyRate();
        CurrencyRateId id = new CurrencyRateId();
        id.setCurrencyCode(currency);
        id.setTimestamp(timestamp);
        fromDb.setId(id);
        fromDb.setRate(rate);

        when(repository.findTopByIdCurrencyCodeOrderByIdTimestampDesc(eq(currency))).thenReturn(fromDb);

        // Act
        Optional<CurrencyResponseDto> result = currencyService.getCachedCurrency(currency);

        // Assert
        assertThat(result).isPresent();
        CurrencyResponseDto dto = result.get();
        assertThat(dto.currencyCode()).isEqualTo("GBP");
        assertThat(dto.rate()).isEqualByComparingTo(rate);
        assertThat(dto.timestamp()).isEqualTo(timestamp);

        // Verifications
        verify(cacheService).getIfFresh(eq(currency));
        verify(repository).findTopByIdCurrencyCodeOrderByIdTimestampDesc(eq(currency));
        verify(cacheService).update(eq(currency), eq(rate), eq(timestamp));
    }

    @Test
    void shouldNotCrash_whenExternalApiFails() {
        // Arrange
        when(exchangeClient.fetchLatestRates(anyString()))
                .thenThrow(new ExternalServiceException("API is down"));

        // Act
        assertThatCode(() -> currencyService.fetchAndSaveRates())
                .doesNotThrowAnyException();

        // Assert
        verify(exchangeClient).fetchLatestRates(eq("USD"));
        verify(repository, never()).saveAll(anyList());
    }

    @Test
    void shouldThrowInvalidCurrencyException_whenUnsupportedCurrency() {
        // Arrange
        String invalidCode = "XYZ";
        doThrow(new InvalidCurrencyException("Unsupported currency: XYZ"))
                .when(validator).validate(invalidCode);

        // Act & Assert
        assertThatThrownBy(() -> currencyService.getRatesForCurrency(invalidCode))
                .isInstanceOf(InvalidCurrencyException.class)
                .hasMessageContaining("Unsupported currency");
    }

}
