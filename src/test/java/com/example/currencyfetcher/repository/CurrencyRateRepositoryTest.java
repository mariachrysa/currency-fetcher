package com.example.currencyfetcher.repository;

import com.example.currencyfetcher.model.CurrencyRate;
import com.example.currencyfetcher.model.CurrencyRateId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class CurrencyRateRepositoryTest {

    @Autowired
    private CurrencyRateRepository repository;

    @Test
    @DisplayName("Save and fetch CurrencyRate by composite key")
    void saveAndFetchCurrencyRate() {
        var id = new CurrencyRateId("USD", LocalDateTime.now());
        var rate = new CurrencyRate(id, new BigDecimal("1.23"));
        repository.save(rate);

        var found = repository.findById(id);
        assertThat(found).isPresent();
        assertThat(found.get().getRate()).isEqualByComparingTo("1.23");
    }

    @Test
    @DisplayName("Get latest CurrencyRate by currency code")
    void findTopByCurrencyCode() {
        var now = LocalDateTime.now();
        var older = now.minusDays(1);
        repository.save(new CurrencyRate(new CurrencyRateId("USD", older), new BigDecimal("1.11")));
        repository.save(new CurrencyRate(new CurrencyRateId("USD", now), new BigDecimal("1.33")));

        var latest = repository.findTopByIdCurrencyCodeOrderByIdTimestampDesc("USD");
        assertThat(latest).isNotNull();
        assertThat(latest.getRate()).isEqualByComparingTo("1.33");
    }

    @Test
    @DisplayName("List all rates by currency code, ordered by timestamp desc")
    void findByCurrencyCodeOrderedDesc() {
        var now = LocalDateTime.now();
        repository.save(new CurrencyRate(new CurrencyRateId("EUR", now.minusHours(1)), new BigDecimal("0.9")));
        repository.save(new CurrencyRate(new CurrencyRateId("EUR", now), new BigDecimal("0.91")));

        List<CurrencyRate> eurRates = repository.findByIdCurrencyCodeOrderByIdTimestampDesc("EUR");
        assertThat(eurRates).hasSize(2);
        assertThat(eurRates.get(0).getRate()).isEqualByComparingTo("0.91");
        assertThat(eurRates.get(1).getRate()).isEqualByComparingTo("0.9");
    }

    @Test
    @DisplayName("Find all distinct currency codes")
    void findAllCurrencyCodes() {
        repository.save(new CurrencyRate(new CurrencyRateId("GBP", LocalDateTime.now()), new BigDecimal("0.8")));
        repository.save(new CurrencyRate(new CurrencyRateId("JPY", LocalDateTime.now()), new BigDecimal("130")));

        List<String> codes = repository.findAllCurrencies();
        assertThat(codes).containsExactlyInAnyOrder("GBP", "JPY");
    }

    @Test
    @DisplayName("Empty repository returns empty lists")
    void returnsEmptyWhenNone() {
        assertThat(repository.findByIdCurrencyCodeOrderByIdTimestampDesc("ZAR")).isEmpty();
        assertThat(repository.findTopByIdCurrencyCodeOrderByIdTimestampDesc("ZAR")).isNull();
        assertThat(repository.findAllCurrencies()).isEmpty();
    }
}
