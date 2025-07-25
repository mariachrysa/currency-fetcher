package com.example.currencyfetcher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyResponseDto {
    private String currency;
    private double rate;
    private LocalDateTime timestamp;
}
