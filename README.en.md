# Digital Banking App

Full-stack digital account: Java 21 + Spring Boot 3 backend and React + TypeScript + Tailwind frontend. MVP scope: create accounts, deposit, withdraw, transfer between accounts, and view statement.

## About the project

The backend exposes a REST API to create accounts, check balance, deposit, withdraw, transfer between accounts (with sufficient-balance validation), and view the statement. The frontend consumes the API and provides screens to manage accounts, make transfers, and view the statement.

Balance is stored with BigDecimal to avoid floating-point errors on monetary values. Business and validation errors are returned in a standardized body (timestamp, status, error, message).

## Stack

- Java 21, Spring Boot 3.4.4 (Web, Data JPA, Validation)
- H2 (in-memory database, jdbc:h2:mem:bancodigital, for development and tests)
- Lombok, JUnit 5, AssertJ
- React 18, TypeScript, Tailwind CSS, Vite, React Router

## Repository structure

```
app-bancario-digital/
  pom.xml
  src/main/java/com/bancodigital/app/  # models, DTOs, exceptions, repositories, services, controllers
  src/test/java/com/bancodigital/app/  # 40 JUnit tests
  frontend/                            # React + TS + Tailwind (Vite)
```

## Prerequisites

- JDK 21
- Maven
- Node.js

## How to use

### Backend

Run the tests:

```bash
cd app-bancario-digital
./mvnw test        # or: mvn test
```

The in-memory H2 database starts automatically; no additional database to install.

Run the application:

```bash
./mvnw spring-boot:run   # or: mvn spring-boot:run
```

The API is available at http://localhost:8080.

### Frontend

```bash
cd frontend
npm install
npm run dev        # http://localhost:5173 (proxy /api to the API on :8080)
npm run build      # production build to frontend/dist
```

## API endpoints

Base: http://localhost:8080

- `POST /contas` — create account (`titular`, `saldoInicial`)
- `GET /contas` — list accounts
- `GET /contas/{id}` — get account by ID
- `POST /contas/{id}/depositar` — deposit (`valor`)
- `POST /contas/{id}/sacar` — withdraw (`valor`)
- `POST /transferencias` — transfer (`contaOrigemId`, `contaDestinoId`, `valor`)
- `GET /extrato/{contaId}` — account statement

Error responses follow the format `{ timestamp, status, error, message }`. Examples: account not found (404), insufficient balance (400), invalid data (400).

## Tests

```bash
./mvnw test
```

Result: 40 tests passing, 0 failures (accounts, transfers with balance validation, statement, repositories, and controller HTTP integration).

## License

MIT

## Author

Kelvin Oliveira
