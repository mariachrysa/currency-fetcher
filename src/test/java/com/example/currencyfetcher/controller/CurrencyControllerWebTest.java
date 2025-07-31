//package com.example.currencyfetcher.controller;
//
//import com.example.currencyfetcher.cache.CachedCurrency;
//import com.example.currencyfetcher.dto.ConvertedCurrencyDto;
//import com.example.currencyfetcher.exceptions.InvalidCurrencyException;
//import com.example.currencyfetcher.model.CurrencyRate;
//import com.example.currencyfetcher.repository.CurrencyRateRepository;
//import com.example.currencyfetcher.service.CurrencyService;
//import org.junit.jupiter.api.Test;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.test.web.servlet.MockMvc;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//@WebMvcTest(CurrencyController.class)
//class CurrencyControllerWebTest {
//
//    @Autowired
//    private MockMvc mockMvc;
//
//    @MockBean
//    private CurrencyService currencyService;
//
//    @MockBean
//    private CurrencyRateRepository repository;
//
//    @Test
//    void testGetLatestRate_valid() throws Exception {
//        CurrencyRate rate = new CurrencyRate(1L, "USD", 1.0, LocalDateTime.now());
//        when(currencyService.getLatestFromDBOrThrow("USD")).thenReturn(rate);
//
//        mockMvc.perform(get("/api/currency/USD"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.currencyCode").value("USD"));
//    }
//
//    @Test
//    void testGetLatestRate_invalid() throws Exception {
//        when(currencyService.getLatestFromDBOrThrow("XXX")).thenThrow(new InvalidCurrencyException("Invalid code: XXX"));
//        when(repository.findAllCurrencies()).thenReturn(List.of("USD", "EUR", "JPY"));
//
//        mockMvc.perform(get("/api/currency/XXX"))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error").value("Invalid code: XXX"))
//                .andExpect(jsonPath("$.validCurrencies").isArray());
//    }
//
//    @Test
//    void testConvertCurrency_valid() throws Exception {
//        ConvertedCurrencyDto dto = new ConvertedCurrencyDto("USD", "JPY", 100, 15000);
//        when(currencyService.convertCurrency("USD", "JPY", 100)).thenReturn(Optional.of(dto));
//
//        mockMvc.perform(get("/api/currency/convert?from=USD&to=JPY&amount=100"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.converted").value(15000));
//    }
//
//    @Test
//    void testConvertCurrency_invalid() throws Exception {
//        when(currencyService.convertCurrency("XXX", "YYY", 100)).thenReturn(Optional.empty());
//        when(repository.findAllCurrencies()).thenReturn(List.of("USD", "EUR", "JPY"));
//
//        mockMvc.perform(get("/api/currency/convert?from=XXX&to=YYY&amount=100"))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error").value("Invalid currency code(s)"))
//                .andExpect(jsonPath("$.validCurrencies").isArray());
//    }
//
//    @Test
//    void testGetCurrencyFromCache_valid() throws Exception {
//        CachedCurrency cached = new CachedCurrency(1.0, LocalDateTime.now());
//        when(currencyService.getCurrencyRate("USD")).thenReturn(Optional.of(cached));
//
//        mockMvc.perform(get("/api/currency/api/currency/USD"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.currency").value("USD"));
//    }
//
//    @Test
//    void testGetCurrencyFromCache_invalid() throws Exception {
//        when(currencyService.getCurrencyRate("XXX")).thenReturn(Optional.empty());
//        when(repository.findAllCurrencies()).thenReturn(List.of("USD", "EUR", "JPY"));
//
//        mockMvc.perform(get("/api/currency/api/currency/XXX"))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error").value("Invalid currency code: XXX"))
//                .andExpect(jsonPath("$.validCurrencies").isArray());
//    }
//}
