package com.example.currencyfetcher.controller;

import com.example.currencyfetcher.model.CachedCurrency;
import com.example.currencyfetcher.model.CurrencyRate;
import com.example.currencyfetcher.repository.CurrencyRateRepository;
import com.example.currencyfetcher.service.CurrencyService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
        List<CurrencyRate> list = repository.findByCurrencyCodeOrderByTimestampDesc(code.toUpperCase());

        if (list.isEmpty()) {
            List<String> all = repository.findAll().stream()
                    .map(CurrencyRate::getCurrencyCode)
                    .distinct()
                    .toList();
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid currency code: " + code,
                    "validCurrencies", all
            ));
        }

        CurrencyRate latest = list.get(0);
        return ResponseEntity.ok(latest);
    }

    /**
     * GET /api/currency/api/currency/{code}
     * Same purpose as above, but uses internal cache if available.
     * Returns rate and timestamp for the given currency.
     */
    @GetMapping("/api/currency/{code}")
    public ResponseEntity<?> getCurrency(@PathVariable String code) {
        Optional<CachedCurrency> result = currencyService.getCurrencyRate(code);

        if (result.isEmpty()) {
            List<String> available = repository.findAllCurrencies(); // You may need a custom query here
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Invalid currency code: " + code, "validCurrencies", available));
        }

        CachedCurrency rate = result.get();
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
        Optional<CachedCurrency> fromRateOpt = currencyService.getCurrencyRate(from);
        Optional<CachedCurrency> toRateOpt = currencyService.getCurrencyRate(to);

        if (fromRateOpt.isEmpty() || toRateOpt.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Invalid currency code(s)",
                    "validCurrencies", repository.findAllCurrencies()
            ));
        }

        double fromRate = fromRateOpt.get().getRate();
        double toRate = toRateOpt.get().getRate();
        double converted = amount * (toRate / fromRate);

        return ResponseEntity.ok(Map.of(
                "from", from.toUpperCase(),
                "to", to.toUpperCase(),
                "amount", amount,
                "converted", converted
        ));
    }

    /**
     * GET /api/currency/filter?minRate=5.0
     * Filters currencies whose latest rate is >= minRate.
     * Useful for finding strong currencies or large conversions.
     */
    @GetMapping("/filter")
    public ResponseEntity<?> filterByMinRate(@RequestParam double minRate) {
        List<CurrencyRate> latestRates = repository.findAll();

        Map<String, CurrencyRate> latestPerCurrency = latestRates.stream()
                .collect(Collectors.toMap(
                        CurrencyRate::getCurrencyCode,
                        cr -> cr,
                        (cr1, cr2) -> cr1.getTimestamp().isAfter(cr2.getTimestamp()) ? cr1 : cr2
                ));

        List<Map<String, Object>> filtered = latestPerCurrency.values().stream()
                .filter(rate -> rate.getRate() >= minRate)
                .map(rate -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("currency", rate.getCurrencyCode());
                    map.put("rate", rate.getRate());
                    map.put("timestamp", rate.getTimestamp().toString());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(filtered);
    }

    /**
     * GET /api/currency/top?limit=5
     * Returns the top N currencies by highest exchange rate.
     * Useful for ranking or analysis.
     */
    @GetMapping("/top")
    public ResponseEntity<?> topCurrencies(@RequestParam(defaultValue = "5") int limit) {
        List<CurrencyRate> latestRates = repository.findAll();

        Map<String, CurrencyRate> latestPerCurrency = latestRates.stream()
                .collect(Collectors.toMap(
                        CurrencyRate::getCurrencyCode,
                        cr -> cr,
                        (cr1, cr2) -> cr1.getTimestamp().isAfter(cr2.getTimestamp()) ? cr1 : cr2
                ));

        List<Map<String, Object>> top = latestPerCurrency.values().stream()
                .sorted((a, b) -> Double.compare(b.getRate(), a.getRate()))
                .limit(limit)
                .map(rate -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("currency", rate.getCurrencyCode());
                    map.put("rate", rate.getRate());
                    return map;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(top);
    }

}