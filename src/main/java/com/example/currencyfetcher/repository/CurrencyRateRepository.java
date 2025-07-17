package com.example.currencyfetcher.repository;

import com.example.currencyfetcher.model.CurrencyRate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, Long> {
    List<CurrencyRate> findByCurrencyCodeOrderByTimestampDesc(String currencyCode);

    @Query("SELECT DISTINCT c.currencyCode FROM CurrencyRate c")
    List<String> findAllCurrencies();

    CurrencyRate findTopByCurrencyCodeOrderByTimestampDesc(String currencyCode);

}
