package com.example.currencyfetcher.util;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
public class CurrencyRateLoader {

    private static final String FILE_NAME = "/currency_codes.txt";

    @Getter
    private final Map<String, BigDecimal> staticRates = new HashMap<>();

    @PostConstruct
    public void loadCurrencyRates() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getResourceAsStream(FILE_NAME)))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=");
                if (parts.length == 2) {
                    String code = parts[0].trim();
                    BigDecimal rate = new BigDecimal(parts[1].trim());
                    staticRates.put(code, rate);
                }
            }

            log.info("Loaded {} static currency rates from file.", staticRates.size());

        } catch (Exception e) {
            log.error("Failed to load static currency rates from file: {}", FILE_NAME, e);
        }
    }
}