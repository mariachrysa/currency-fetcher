package com.example.currencyfetcher.controller;

import com.example.currencyfetcher.dto.ConvertedCurrencyDto;
import com.example.currencyfetcher.dto.CurrencyRateHistoryDto;
import com.example.currencyfetcher.dto.CurrencyResponseDto;
import com.example.currencyfetcher.exceptions.ExternalServiceException;
import com.example.currencyfetcher.exceptions.InvalidCurrencyException;
import com.example.currencyfetcher.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;
import static org.hamcrest.Matchers.closeTo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CurrencyController.class)
class CurrencyControllerWebTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CurrencyService currencyService;

    private CurrencyResponseDto usdResponse;
    private CurrencyRateHistoryDto historyDto;
    private ConvertedCurrencyDto converted;

    @BeforeEach
    void setUp() {
        usdResponse = new CurrencyResponseDto("USD", new BigDecimal("1.0000"), LocalDateTime.now());
        historyDto = new CurrencyRateHistoryDto("USD", new BigDecimal("1.0000"), LocalDateTime.now());
        converted = new ConvertedCurrencyDto("USD", "EUR", new BigDecimal("10.00"), new BigDecimal("9.5000"));
    }

    @Test
    void testCurrencyRatesEndpoint() throws Exception {
        // Arrange
        String currencyCode = "USD";
        CurrencyResponseDto response = new CurrencyResponseDto(
                currencyCode,
                new BigDecimal("1.2345"),
                LocalDateTime.of(2024, 1, 1, 12, 0)
        );

        when(currencyService.getRatesForCurrency(currencyCode)).thenReturn(response);

        // Act & Assert
        mockMvc.perform(get("/api/currency/{code}", currencyCode))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currencyCode").value("USD"))
                .andExpect(jsonPath("$.rate").value(1.2345))
                .andExpect(jsonPath("$.timestamp").exists());

        verify(currencyService).getRatesForCurrency("USD");
    }

    @Test
    void testConvertEndpoint() throws Exception {
        when(currencyService.convert(anyString(), anyString(), any())).thenReturn(converted);

        mockMvc.perform(get("/api/currency/convert")
                        .param("from", "USD")
                        .param("to", "EUR")
                        .param("amount", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.from").value("USD"))
                .andExpect(jsonPath("$.to").value("EUR"))
                .andExpect(jsonPath("$.amount").value(10))
                .andExpect(jsonPath("$.convertedAmount").value(9.5));


        verify(currencyService).convert("USD", "EUR", new BigDecimal("10"));
    }

    @Test
    void testFilterEndpoint() throws Exception {
        when(currencyService.filterByMinRate(anyDouble())).thenReturn(List.of(usdResponse));

        mockMvc.perform(get("/api/currency/filter")
                        .param("minRate", "0.5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].currencyCode").value("USD"))
                .andExpect(jsonPath("$[0].rate", closeTo(1.0, 0.0001)));

        verify(currencyService).filterByMinRate(0.5);
    }

    @Test
    void testTopCurrenciesEndpoint() throws Exception {
        when(currencyService.getTopCurrencies(anyInt())).thenReturn(List.of(usdResponse));

        mockMvc.perform(get("/api/currency/top")
                        .param("limit", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].currencyCode").value("USD"));

        verify(currencyService).getTopCurrencies(1);
    }

    @Test
    void testHistoryEndpoint() throws Exception {
        when(currencyService.getHistoryForCurrency(eq("USD"))).thenReturn(List.of(historyDto));

        mockMvc.perform(get("/api/currency/history/USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].currencyCode").value("USD"))
                .andExpect(jsonPath("$[0].rate", closeTo(1.0, 0.0001)));

        verify(currencyService).getHistoryForCurrency("USD");
    }

    @Test
    void shouldReturn400_whenInvalidCurrencyProvided() throws Exception {
        when(currencyService.getRatesForCurrency(eq("XYZ")))
                .thenThrow(new InvalidCurrencyException("Unsupported currency"));

        mockMvc.perform(get("/api/currency/XYZ"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.error").value("Invalid Currency"))
                .andExpect(jsonPath("$.message").value("Invalid currency code: Unsupported currency"))
                .andExpect(jsonPath("$.path").value("/api/currency/XYZ"));
    }

    @Test
    void shouldReturn503_whenExternalServiceFails() throws Exception {
        when(currencyService.getRatesForCurrency(eq("USD")))
                .thenThrow(new ExternalServiceException("Upstream API error"));

        mockMvc.perform(get("/api/currency/USD"))
                .andExpect(status().isServiceUnavailable())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(jsonPath("$.error").value("Service Unavailable"))
                .andExpect(jsonPath("$.message").value("Upstream API error"))
                .andExpect(jsonPath("$.path").value("/api/currency/USD"));
    }

}
