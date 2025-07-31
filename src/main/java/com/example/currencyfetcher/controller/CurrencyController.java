package com.example.currencyfetcher.controller;

import com.example.currencyfetcher.dto.ConvertedCurrencyDto;
import com.example.currencyfetcher.dto.CurrencyRateHistoryDto;
import com.example.currencyfetcher.dto.CurrencyResponseDto;
import com.example.currencyfetcher.service.CurrencyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Pattern;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/currency")
@RequiredArgsConstructor
@Validated
@Tag(name = "Currency API", description = "Endpoints for exchange rates and currency conversion")
public class CurrencyController {

    private final CurrencyService currencyService;

    @Operation(summary = "Get exchange rate for a currency code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful retrieval",
                    content = @Content(schema = @Schema(implementation = CurrencyResponseDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid currency code"),
            @ApiResponse(responseCode = "404", description = "Currency not found")
    })
    @GetMapping("/{code}")
    public ResponseEntity<CurrencyResponseDto> getCurrencyRates(
            @PathVariable("code")
            @Pattern(regexp = "^[A-Z]{3}$", message = "Currency code must be 3 uppercase letters")
            String code) {

        CurrencyResponseDto responseDto = currencyService.getRatesForCurrency(code);
        return ResponseEntity.ok(responseDto);
    }

    @Operation(summary = "Convert currency between two codes")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful conversion",
                    content = @Content(schema = @Schema(implementation = ConvertedCurrencyDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid currency code or amount"),
            @ApiResponse(responseCode = "404", description = "Currency not found")
    })
    @GetMapping("/convert")
    public ResponseEntity<ConvertedCurrencyDto> convertCurrency(
            @RequestParam("from")
            @Pattern(regexp = "^[A-Z]{3}$", message = "From currency must be 3 uppercase letters")
            String from,

            @RequestParam("to")
            @Pattern(regexp = "^[A-Z]{3}$", message = "To currency must be 3 uppercase letters")
            String to,

            @RequestParam("amount")
            @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be greater than 0")
            BigDecimal amount) {

        ConvertedCurrencyDto result = currencyService.convert(from, to, amount);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Filter currencies by minimum exchange rate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "List of currencies with rate above minRate",
                    content = @Content(schema = @Schema(implementation = CurrencyResponseDto.class)))
    })
    @GetMapping("/filter")
    public ResponseEntity<List<CurrencyResponseDto>> filterByMinRate(
            @RequestParam("minRate")
            @DecimalMin(value = "0.0", inclusive = false, message = "minRate must be greater than 0")
            double minRate) {

        List<CurrencyResponseDto> filtered = currencyService.filterByMinRate(minRate);
        return ResponseEntity.ok(filtered);
    }

    @Operation(summary = "Get top currencies by rate")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Top currencies returned successfully",
                    content = @Content(schema = @Schema(implementation = CurrencyResponseDto.class)))
    })
    @GetMapping("/top")
    public ResponseEntity<List<CurrencyResponseDto>> topCurrencies(
            @RequestParam(name = "limit", defaultValue = "5")
            int limit) {

        List<CurrencyResponseDto> topCurrencies = currencyService.getTopCurrencies(limit);
        return ResponseEntity.ok(topCurrencies);
    }

    @Operation(summary = "Get historical rates for a currency code")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Historical data returned successfully",
                    content = @Content(schema = @Schema(implementation = CurrencyRateHistoryDto.class))),
            @ApiResponse(responseCode = "400", description = "Invalid currency code"),
            @ApiResponse(responseCode = "404", description = "Currency not found")
    })
    @GetMapping("/history/{code}")
    public ResponseEntity<List<CurrencyRateHistoryDto>> getHistory(
            @PathVariable("code")
            @Pattern(regexp = "^[A-Z]{3}$", message = "Currency code must be 3 uppercase letters")
            String code) {

        List<CurrencyRateHistoryDto> history = currencyService.getHistoryForCurrency(code);
        return ResponseEntity.ok(history);
    }
}
