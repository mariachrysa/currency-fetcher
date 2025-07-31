package com.example.currencyfetcher.validation;

import com.example.currencyfetcher.exceptions.InvalidCurrencyException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Component
public class CurrencyValidator {

    private final Set<String> supportedCodes = new HashSet<>();

    @PostConstruct
    public void loadSupportedCodes() {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(getClass().getClassLoader().getResourceAsStream("currency_codes.txt")))) {

            if (reader == null) {
                throw new IllegalStateException("currency_codes.txt not found in resources!");
            }

            String line;
            while ((line = reader.readLine()) != null) {
                supportedCodes.add(line.trim().toUpperCase());
            }

            log.info("Loaded {} supported currency codes", supportedCodes.size());

        } catch (Exception e) {
            throw new RuntimeException("Failed to load currency codes", e);
        }
    }

    public void validate(String code) {
        if (!isSupported(code)) {
            throw new InvalidCurrencyException("Unsupported currency code: " + code);
        }
    }

    public boolean isSupported(String code) {
        return supportedCodes.contains(code.toUpperCase());
    }
}
