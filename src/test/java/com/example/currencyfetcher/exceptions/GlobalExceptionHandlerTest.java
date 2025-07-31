//package com.example.currencyfetcher.exceptions;
//
//import com.example.currencyfetcher.controller.CurrencyController;
//import com.example.currencyfetcher.repository.CurrencyRateRepository;
//import com.example.currencyfetcher.service.CurrencyService;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.web.servlet.MockMvc;
//
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@WebMvcTest(CurrencyController.class)
//class GlobalExceptionHandlerTest {
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
//    void testInvalidCurrencyExceptionIsHandled() throws Exception {
//        when(currencyService.getLatestFromDBOrThrow("ABC"))
//                .thenThrow(new InvalidCurrencyException("ABC"));
//
//        mockMvc.perform(get("/api/currency/ABC"))
//                .andExpect(status().isBadRequest())
//                .andExpect(jsonPath("$.error").value("Invalid currency code: ABC"))
//                .andExpect(jsonPath("$.validCurrencies").isArray());
//    }
//}
