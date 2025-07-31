//package com.example.currencyfetcher.service.impl;
//
//import com.example.currencyfetcher.cache.CachedCurrency;
//import com.example.currencyfetcher.dto.CurrencyResponseDto;
//import com.example.currencyfetcher.dto.ConvertedCurrencyDto;
//import com.example.currencyfetcher.exceptions.InvalidCurrencyException;
//import com.example.currencyfetcher.model.CurrencyRate;
//import com.example.currencyfetcher.repository.CurrencyRateRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.modelmapper.ModelMapper;
//
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//class CurrencyServiceImplTest {
//
//    private CurrencyRateRepository repository;
//    private CurrencyServiceImpl service;
//
//    @BeforeEach
//    void setUp() {
//        repository = mock(CurrencyRateRepository.class);
//        service = new CurrencyServiceImpl(repository, "dummy-api-key", );
//    }
//
//    @Test
//    void getLatestFromDBOrThrow_validCurrency_returnsRate() {
//        CurrencyRate mock = new CurrencyRate(1L, "USD", 1.0, LocalDateTime.now());
//        when(repository.findTopByCurrencyCodeOrderByTimestampDesc("USD")).thenReturn(mock);
//
//        CurrencyRate result = service.getLatestFromDBOrThrow("USD");
//
//        assertEquals("USD", result.getCurrencyCode());
//    }
//
//    @Test
//    void getLatestFromDBOrThrow_invalidCurrency_throwsException() {
//        when(repository.findTopByCurrencyCodeOrderByTimestampDesc("XXX")).thenReturn(null);
//
//        assertThrows(InvalidCurrencyException.class, () -> service.getLatestFromDBOrThrow("XXX"));
//    }
//
//    @Test
//    void getCurrencyRate_cacheMiss_returnsFromDB() {
//        CurrencyRate mock = new CurrencyRate(null, "EUR", 1.3, LocalDateTime.now());
//        when(repository.findTopByCurrencyCodeOrderByTimestampDesc("EUR")).thenReturn(mock);
//
//        Optional<CachedCurrency> result = service.getCurrencyRate("EUR");
//
//        assertTrue(result.isPresent());
//        assertEquals(1.3, result.get().getRate());
//    }
//
//    @Test
//    void convertCurrency_validInputs_returnsConvertedDto() {
//        LocalDateTime now = LocalDateTime.now();
//        when(repository.findTopByCurrencyCodeOrderByTimestampDesc("USD"))
//                .thenReturn(new CurrencyRate(null, "USD", 1.0, now));
//        when(repository.findTopByCurrencyCodeOrderByTimestampDesc("JPY"))
//                .thenReturn(new CurrencyRate(null, "JPY", 150.0, now));
//
//        Optional<ConvertedCurrencyDto> result = service.convertCurrency("USD", "JPY", 100);
//
//        assertTrue(result.isPresent());
//        assertEquals(15000.0, result.get().getConverted());
//    }
//
//    @Test
//    void filterByMinRate_returnsFilteredDtos() {
//        LocalDateTime now = LocalDateTime.now();
//        List<CurrencyRate> mockRates = List.of(
//                new CurrencyRate(null, "USD", 1.0, now),
//                new CurrencyRate(null, "BTC", 30000.0, now)
//        );
//        when(repository.findAll()).thenReturn(mockRates);
//
//        List<CurrencyResponseDto> result = service.filterByMinRate(5000.0);
//
//        assertEquals(1, result.size());
//        assertEquals("BTC", result.get(0).getCurrency());
//    }
//}
