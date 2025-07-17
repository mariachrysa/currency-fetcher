# Currency Fetcher App (Spring Boot)

**Author:** Maria Chrysanthou  
**Date:** 17/07/2025

A Spring Boot backend service that fetches live currency exchange rates from the API Layer API, stores them in a database, caches them, and exposes multiple REST endpoints to query, convert, and filter them.

---

## Features

- Fetches live rates every 60 seconds and persists them in the DB
- Caches rates in-memory to reduce DB/API load
- Convert currencies using latest fetched rates
- Filter currencies by min rate
- Get top N currencies with highest rate
- Validate and list supported currencies if input is invalid

---

## Tech Stack

- Java 17+
- Spring Boot
- Spring Scheduler
- Spring Web (WebClient)
- H2 Database (in-memory)
- Maven
- Lombok

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
   exchange.api.key=YOUR_API_KEY_HERE
   ```

3. **Run the app**

   Run using Maven:
   ```bash
   ./mvnw spring-boot:run
   ```

---

## REST Endpoints

### 1. Get Latest Rate
```http
GET /api/currency/{code}
```
Returns the latest rate from DB.

---

### 2. Get Cached Rate
```http
GET /api/currency/api/currency/{code}
```
Returns rate from in-memory cache (if not expired).

---

### 3. Convert Between Currencies
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

### 4. Filter by Min Rate
```http
GET /api/currency/filter?minRate=5.0
```
Returns all currencies whose latest rate is ≥ 5.0.

---

### 5. Top N Currencies by Rate
```http
GET /api/currency/top?limit=5
```
Returns the top 5 currencies by exchange rate.

---

## How the Scheduling Works

Every 60 seconds, the app sends a request to:

```http
https://api.apilayer.com/exchangerates_data/latest
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
├── controller         // REST APIs
├── service            // Business logic & caching
├── model              // Entity + Cache 
├── repository         // Spring Data JPA
└── resources
    └── application.properties
```
