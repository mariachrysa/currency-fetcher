package com.example.currencyfetcher.controller;

import com.example.currencyfetcher.cache.CachedCurrency;
import com.example.currencyfetcher.model.CurrencyRate;
import com.example.currencyfetcher.repository.CurrencyRateRepository;
import com.example.currencyfetcher.service.CurrencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/currency")
public class CurrencyController {

    private final CurrencyRateRepository repository;
    private final CurrencyService currencyService;

    public CurrencyController(CurrencyRateRepository repository, CurrencyService currencyService) {
        this.repository = repository;
        this.currencyService = currencyService;
    }

    /**
     * GET /api/currency/{code}
     * Fetches the latest exchange rate from the DB for a specific currency code (e.g., USD).
     * If the currency code is invalid, returns all valid codes for reference.
     */
    @GetMapping("/{code}")
    public ResponseEntity<?> getLatestRate(@PathVariable String code) {
        CurrencyRate result = currencyService.getLatestFromDBOrThrow(code);
        return ResponseEntity.ok(result);
    }


    /**
     * GET /api/currency/{code}
     * Same purpose as above, but uses internal cache if available.
     * Returns rate and timestamp for the given currency.
     */
    @GetMapping("/api/currency/{code}")
    public ResponseEntity<?> getCurrency(@PathVariable String code) {
        CachedCurrency rate = currencyService.getCachedCurrencyOrThrow(code);
        return ResponseEntity.ok(Map.of(
                "currency", code.toUpperCase(),
                "rate", rate.getRate(),
                "timestamp", rate.getTimestamp().toString()
        ));
    }


    /**
     * GET /api/currency/convert?from=USD&to=JPY&amount=100
     * Converts `amount` of `from` currency into the `to` currency using the latest rates.
     * If either currency is not found, responds with error and available codes.
     */
    @GetMapping("/convert")
    public ResponseEntity<?> convertCurrency(@RequestParam String from,
                                             @RequestParam String to,
                                             @RequestParam double amount) {
        return ResponseEntity.ok(currencyService.convertCurrencyOrThrow(from, to, amount));
    }


    /**
     * GET /api/currency/filter?minRate=5.0
     * Filters currencies whose latest rate is >= minRate.
     * Useful for finding strong currencies or large conversions.
     */
    @GetMapping("/filter")
    public ResponseEntity<?> filterByMinRate(@RequestParam double minRate) {
        return ResponseEntity.ok(currencyService.filterByMinRate(minRate));
    }

    /**
     * GET /api/currency/top?limit=5
     * Returns the top N currencies by highest exchange rate.
     * Useful for ranking or analysis.
     */
    @GetMapping("/top")
    public ResponseEntity<?> topCurrencies(@RequestParam(defaultValue = "5") int limit) {
        return ResponseEntity.ok(currencyService.getTopCurrencies(limit));
    }

}