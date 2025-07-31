package com.example.currencyfetcher.scheduler;

import com.example.currencyfetcher.service.CurrencyService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CurrencyRateScheduler {

    private final CurrencyService currencyService;

    @Scheduled(fixedRate = 60_000)
    @Transactional
    public void fetchAndPersistRates() {
        try {
            currencyService.fetchAndSaveRates();
        } catch (Exception ex) {
            log.error("Scheduled fetch failed", ex);
        }
    }
}
