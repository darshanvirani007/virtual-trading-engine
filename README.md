# Virtual Trading Engine

![Java](https://img.shields.io/badge/Java-17-blue)
![Spring Boot](https://img.shields.io/badge/SpringBoot-Backend-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue)
![Docker](https://img.shields.io/badge/Docker-Containerized-blue)
![Maven](https://img.shields.io/badge/Maven-Build-orange)

## Overview

Virtual Trading Engine is a backend-only stock market paper trading platform built using Java and Spring Boot.

The platform allows users to simulate stock trading using real-time market prices without risking real money. Users can register accounts, authenticate securely, execute buy and sell trades, monitor portfolio performance, and track transaction history.

The project was designed using a clean layered architecture and follows backend engineering best practices including authentication, database migrations, caching, testing, API documentation, and containerization.

---

## Features

### Authentication & Security

- User registration
- User login
- JWT authentication
- Refresh token support
- Refresh token rotation
- Logout functionality
- Password hashing using Spring Security

### Trading Engine

- Simulated stock purchases
- Simulated stock sales
- Balance validation
- Portfolio updates
- Transaction recording

### Portfolio Management

- Portfolio holdings tracking
- Market value calculation
- Total equity calculation
- Unrealized profit and loss tracking

### Market Data Integration

- Alpha Vantage API integration
- Real-time stock quote retrieval
- Cached quote responses using Caffeine Cache

### Database

- PostgreSQL database
- Flyway migrations
- JPA/Hibernate ORM
- Repository pattern

### Infrastructure

- Dockerized database
- Docker Compose support
- Environment-based configuration

### Monitoring & Logging

- Spring Boot Actuator
- Structured logging
- Request correlation IDs

### Documentation

- Swagger/OpenAPI support

### Testing

- Unit tests
- Service tests
- Integration tests

---

## System Architecture

```text
Client (Web / Mobile)
        │
        ▼
Spring Boot Controllers
        │
        ▼
Service Layer (Business Logic)
        │
        ▼
Repository Layer (Spring Data JPA)
        │
        ▼
PostgreSQL Database

External Market Data
        │
        ▼
Alpha Vantage API
```

---

## Architecture Layers

### Controller Layer

Responsible for receiving HTTP requests and returning HTTP responses.

Examples:

- AuthController
- TradeController
- PortfolioController
- StockController

Example flow:

```text
Request
   │
   ▼
Controller
   │
   ▼
Service
```

---

### Service Layer

Contains business logic.

Examples:

- AuthService
- TradeService
- PortfolioService

Responsibilities:

- User authentication
- Trade execution
- Portfolio calculations
- Refresh token management

---

### Repository Layer

Responsible for database access.

Examples:

- UserRepository
- TransactionRepository
- PortfolioItemRepository
- RefreshTokenRepository

Uses Spring Data JPA to communicate with PostgreSQL.

---

### Entity Layer

Represents database tables.

Examples:

- User
- PortfolioItem
- Transaction
- RefreshToken
- Stock

---

## Authentication Flow

### Login Process

```text
User Login Request
        │
        ▼
AuthenticationManager
        │
        ▼
Validate Username & Password
        │
        ▼
Generate JWT Access Token
        │
        ▼
Generate Refresh Token
        │
        ▼
Return Tokens
```

### JWT Authentication

Protected endpoints require:

```http
Authorization: Bearer <access_token>
```

### Refresh Token Flow

```text
Expired Access Token
          │
          ▼
Refresh Endpoint
          │
          ▼
Validate Refresh Token
          │
          ▼
Generate New Access Token
          │
          ▼
Rotate Refresh Token
```

---

## Trading Flow

### Buy Stock

```text
Buy Request
     │
     ▼
Fetch Current Stock Price
     │
     ▼
Calculate Total Cost
     │
     ▼
Validate User Balance
     │
     ▼
Update Portfolio
     │
     ▼
Save Transaction
```

### Sell Stock

```text
Sell Request
     │
     ▼
Validate Holdings
     │
     ▼
Fetch Current Price
     │
     ▼
Update Balance
     │
     ▼
Update Portfolio
     │
     ▼
Save Transaction
```

---

## Portfolio Calculations

### Market Value

```text
Market Value = Quantity × Current Price
```

### Total Equity

```text
Total Equity = Cash Balance + Market Value
```

### Unrealized Profit/Loss

```text
(Current Price - Average Purchase Price)
× Quantity
```

---

## Database Design

### users

Stores user account information.

```text
id
username
email
password
balance
role
```

### portfolio_items

Stores current holdings.

```text
id
user_id
symbol
quantity
average_price
```

### transactions

Stores trade history.

```text
id
user_id
symbol
type
quantity
price
timestamp
```

### refresh_tokens

Stores refresh tokens.

```text
id
token
user_id
expires_at
revoked
```

### stocks

Stores stock metadata.

```text
symbol
company_name
```

---

## Project Structure

```text
virtual-trading-engine
│
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.virtualtradingengine.virtual_trading_engine
│   │   │
│   │   ├── controller
│   │   ├── service
│   │   ├── repository
│   │   ├── entity
│   │   ├── dto
│   │   ├── security
│   │   ├── config
│   │   └── exception
│   │
│   └── resources
│       ├── application.yml
│       └── db/migration
│
├── src/test
│
├── Dockerfile
├── docker-compose.yml
├── pom.xml
└── README.md
```

---

## API Endpoints

### Authentication

```http
POST /api/v1/auth/register
POST /api/v1/auth/login
POST /api/v1/auth/refresh
POST /api/v1/auth/logout
```

### Trading

```http
POST /api/v1/trades/buy
POST /api/v1/trades/sell
GET  /api/v1/trades/history
```

### Portfolio

```http
GET /api/v1/portfolio
GET /api/v1/portfolio/summary
```

### Market Data

```http
GET /api/v1/stocks/{symbol}/quote
```

---

## Example Requests

### Register User

```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
-H "Content-Type: application/json" \
-d '{
  "username":"darshan",
  "email":"darshan@example.com",
  "password":"pass123",
  "startingBalance":10000
}'
```

### Login

```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
-H "Content-Type: application/json" \
-d '{
  "username":"darshan",
  "password":"pass123"
}'
```

### Buy Stock

```bash
curl -X POST http://localhost:8080/api/v1/trades/buy \
-H "Authorization: Bearer TOKEN" \
-H "Content-Type: application/json" \
-d '{
  "symbol":"IBM",
  "quantity":5
}'
```

---

## Running the Project

### Start PostgreSQL

```bash
docker compose up -d
```

### Run Application

```bash
mvn spring-boot:run
```

Application URL:

```text
http://localhost:8080
```

---

## API Documentation

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

---

## Monitoring

Actuator Health Endpoint:

```text
GET /actuator/health
```

---

## Testing

Run all tests:

```bash
mvn test
```

Includes:

- Unit tests
- Service tests
- Integration tests

---

## Technology Stack

### Backend

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA

### Database

- PostgreSQL
- Flyway

### Infrastructure

- Docker
- Docker Compose

### Testing

- JUnit
- Mockito

### Documentation

- Swagger / OpenAPI

### Performance

- Caffeine Cache

---

## Author

Darshan Virani
