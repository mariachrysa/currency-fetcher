package com.example.currencyfetcher.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyRateId implements Serializable {
    private String currencyCode;
    private LocalDateTime timestamp;
}
