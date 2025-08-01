# Currency Fetcher App (Spring Boot)

**Author:** Maria Chrysanthou  
**Date:** 30/07/2025

A Spring Boot backend service that fetches live currency exchange rates from the [ExchangeRate API](https://www.exchangerate-api.com), stores them in a database, caches them, and exposes multiple REST endpoints to query, convert, and filter them.

---

## Featuresbgh 

- Fetches live rates every 60 seconds (scheduled)
- Caches rates in-memory to reduce DB/API load
- DTOs used for clean and secure API responses
- Convert currencies using latest fetched rates
- Filter currencies by min rate
- Get top N currencies with highest rate
- Global exception handling using `@RestControllerAdvice` for clean error responses
- Swagger/OpenAPI docs generated with `springdoc-openapi`

---

## Tech Stack

- Java 17+
- Spring Boot
- Spring Scheduler
- Spring Web (WebClient)
- H2 Database (in-memory)
- ModelMapper
- Maven
- Lombok
- OpenAPI 3 (springdoc-openapi)
- SLF4J Logging

---

## How to Run

1. **Clone the project**
   ```bash
   git clone https://your-repo-url.git
   cd currency-fetcher
   ```

2. **Add your API Key**

   Create (or update) your `application.properties`:
   ```properties
   exchange.api.key=YOUR_API_KEY
   exchange.api.base-url=https://v6.exchangerate-api.com/v6

   ```

3. **Run the app**

   Run using Maven:
   ```bash
   ./mvnw spring-boot:run
   ```

---

## REST Endpoints

### 1. Get Cached Rate
```http
GET /api/currency/{code}
```
Returns rate from in-memory cache (if not expired).

---

### 2. Convert Between Currencies
```http
GET /api/currency/convert?from=USD&to=JPY&amount=100
```
Returns:
```json
{
  "from": "USD",
  "to": "JPY",
  "amount": 100,
  "converted": 14839.35
}
```

---

### 3. Filter by Min Rate
```http
GET /api/currency/filter?minRate=5.0
```
Returns all currencies whose latest rate is ≥ 5.0.

---

### 4. Top N Currencies by Rate
```http
GET /api/currency/top?limit=5
```
Returns the top 5 currencies by exchange rate.

---

### 3. Historical Rates 
```http
GET /api/currency/history/{code}
```
Returns historical records (sorted by timestamp DESC) for a given currency.

---

## How the Scheduling Works

Every 60 seconds, the app sends a request to:

```http
https://v6.exchangerate-api.com/v6/{API_KEY}/latest/USD
```

- Parses the `rates` map
- Saves each currency's rate in the DB with a timestamp

---

## Notes

- The cache expires every 60 seconds
- If a rate isn't found in the cache, the DB is used as fallback
- H2 console (if enabled) is available at:
  ```bash
  http://localhost:8080/h2-console
  ```

---

## Project Structure

```cpp
com.example.currencyfetcher
├── cache/                 # In-memory caching layer
│   ├── CacheService.java
│   └── CachedCurrency.java
├── clients/               # WebClient wrapper
│   └── ExchangeClient.java
├── config/                # Configurations & properties
│   ├── CurrencyApiProperties.java
│   └── CurrencyApiWebClientConfig.java
├── controller/            # REST endpoints
│   └── CurrencyController.java
├── dto/                   # Immutable API response/request models
│   ├── ConvertedCurrencyDto.java
│   ├── CurrencyApiResponseDto.java
│   ├── CurrencyRateHistoryDto.java
│   ├── CurrencyResponseDto.java
│   └── ErrorResponseDto.java
├── exceptions/            # Custom exceptions & handlers
│   ├── ExternalServiceException.java
│   ├── GlobalExceptionHandler.java
│   └── InvalidCurrencyException.java
├── model/                 # JPA entities
│   ├── CurrencyRate.java
│   └── CurrencyRateId.java
├── repository/            # Spring Data JPA interfaces
│   └── CurrencyRateRepository.java
├── scheduler/             # Scheduled fetch logic
│   └── CurrencyRateScheduler.java
├── service/               # Interfaces and business logic
│   ├── CurrencyService.java
│   └── impl/
│       └── CurrencyServiceImpl.java
├── util/                  # Utilities (file loader)
│   └── CurrencyRateLoader.java
├── validation/            # Input validator
│   └── CurrencyValidator.java
├── CurrencyFetcherApplication.java
└── resources/
    ├── application.yml
    └── currency_codes.txt


