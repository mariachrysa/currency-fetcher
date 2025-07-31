package com.example.currencyfetcher.model;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "currency_rates", indexes = {
        @Index(name = "idx_currency_timestamp", columnList = "currency_code, timestamp")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyRate {

    @EmbeddedId
    private CurrencyRateId id;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal rate;

    public CurrencyRate(String currencyCode, BigDecimal rate, LocalDateTime timestamp) {
        this.id = new CurrencyRateId(currencyCode, timestamp);
        this.rate = rate.setScale(4, RoundingMode.HALF_UP);
    }

    public String getCurrencyCode() {
        return id.getCurrencyCode();
    }

    public LocalDateTime getTimestamp() {
        return id.getTimestamp();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CurrencyRate that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
