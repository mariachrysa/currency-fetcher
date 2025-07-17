package com.example.currencyfetcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class CurrencyFetcherApplication {
    public static void main(String[] args) {
        SpringApplication.run(CurrencyFetcherApplication.class, args);

    }
}