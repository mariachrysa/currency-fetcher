package com.example.currencyfetcher.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;

@Schema(description = "Details of an error response")
public record ErrorResponseDto(
        @Schema(description = "Timestamp of the error", example = "2024-07-31T12:00:00Z")
        Instant timestamp,

        @Schema(description = "HTTP status code", example = "400")
        int status,

        @Schema(description = "Error title", example = "Invalid Currency")
        String error,

        @Schema(description = "Detailed error message")
        String message,

        @Schema(description = "Request path that caused the error", example = "/api/currency/XYZ")
        String path
) {
}
