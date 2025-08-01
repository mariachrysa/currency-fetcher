package com.example.currencyfetcher.cache;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CachedCurrency {
    private String code;
    private BigDecimal rate;
    private LocalDateTime timestamp;
}
