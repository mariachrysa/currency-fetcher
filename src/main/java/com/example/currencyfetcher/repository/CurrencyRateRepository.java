package com.example.currencyfetcher.repository;

import com.example.currencyfetcher.model.CurrencyRate;
import com.example.currencyfetcher.model.CurrencyRateId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CurrencyRateRepository extends JpaRepository<CurrencyRate, CurrencyRateId> {

    List<CurrencyRate> findByIdCurrencyCodeOrderByIdTimestampDesc(String currencyCode);

    CurrencyRate findTopByIdCurrencyCodeOrderByIdTimestampDesc(String currencyCode);

    @Query("SELECT DISTINCT c.id.currencyCode FROM CurrencyRate c")
    List<String> findAllCurrencies();
}
