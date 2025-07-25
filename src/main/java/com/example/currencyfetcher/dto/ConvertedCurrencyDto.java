package com.example.currencyfetcher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ConvertedCurrencyDto {
    private String from;
    private String to;
    private double amount;
    private double converted;
}
