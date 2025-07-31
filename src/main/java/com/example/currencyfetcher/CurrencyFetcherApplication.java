package com.example.currencyfetcher;

import com.example.currencyfetcher.config.CurrencyApiProperties;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@OpenAPIDefinition(
        info = @Info(
                title = "Currency Exchange API",
                version = "1.0",
                description = "Fetch and convert real-time currency rates"
        )
)
@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties(CurrencyApiProperties.class)
public class CurrencyFetcherApplication {
    public static void main(String[] args) {
        SpringApplication.run(CurrencyFetcherApplication.class, args);

    }
}

