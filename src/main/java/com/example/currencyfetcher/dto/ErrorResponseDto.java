package com.example.currencyfetcher.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ErrorResponseDto {
    private String error;
    private List<String> validCurrencies;
}
