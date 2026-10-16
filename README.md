# App Bancário Digital / Digital Banking App

Conta digital com **Java 21 + Spring Boot 3** no backend e **React + TypeScript + Tailwind** no frontend. Escopo MVP: contas, transferências e extrato.

Digital account with **Java 21 + Spring Boot 3** backend and **React + TypeScript + Tailwind** frontend. MVP scope: accounts, transfers, and statement.

---

## Português / Portuguese

### Descrição
Aplicação full-stack de conta digital. O backend expõe uma API REST para criar contas, consultar saldo, depositar, sacar, transferir entre contas (com validação de saldo suficiente) e consultar o extrato. O frontend consome essa API e oferece telas para gerenciar contas, fazer transferências e visualizar o extrato.

### Stack
- Java 21, Spring Boot 3 (Web, Data JPA, Validation)
- H2 (banco em memória para desenvolvimento e testes)
- Lombok, JUnit 5, AssertJ
- React 18, TypeScript, Tailwind CSS, Vite, React Router

### Estrutura
```
app-bancario-digital/
  pom.xml
  src/main/java/com/bancodigital/app/  # modelos, DTOs, repositórios, serviços, controladores
  src/test/java/com/bancodigital/app/  # 40 testes JUnit
  frontend/                            # React + TS + Tailwind (Vite)
```

### Como usar

Pré-requisitos: JDK 21, Maven, Node.js.

#### Backend
```bash
cd app-bancario-digital
./mvnw test        # ou: mvn test
```
O banco H2 em memória é iniciado automaticamente; não é preciso instalar nada adicional.

Para rodar a aplicação:
```bash
./mvnw spring-boot:run   # ou: mvn spring-boot:run
```
A API sobe em `http://localhost:8080`.

#### Frontend
```bash
cd frontend
npm install
npm run dev        # http://localhost:5173 (proxy para a API em :8080)
npm run build      # build de produção em frontend/dist
```

### Endpoints principais
- `POST /contas` — criar conta (`titular`, `saldoInicial`)
- `GET /contas` — listar contas
- `GET /contas/{id}` — buscar conta
- `POST /contas/{id}/depositar` — depósito (`valor`)
- `POST /contas/{id}/sacar` — saque (`valor`)
- `POST /transferencias` — transferência (`contaOrigemId`, `contaDestinoId`, `valor`)
- `GET /extrato/{contaId}` — extrato da conta

### Testes
```bash
./mvnw test
```
Resultado: **40 testes passando, 0 falhas** (cobertura de contas, transferências com validação de saldo, extrato, repositórios e integração HTTP dos controladores).

### Licença
MIT

### Autor
Kelvin Oliveira

---

## English / Inglês

### Description
Full-stack digital banking application. The backend exposes a REST API to create accounts, check balance, deposit, withdraw, transfer between accounts (with sufficient-balance validation), and view the statement. The frontend consumes the API and provides screens to manage accounts, make transfers, and view the statement.

### Stack
- Java 21, Spring Boot 3 (Web, Data JPA, Validation)
- H2 (in-memory database for development and tests)
- Lombok, JUnit 5, AssertJ
- React 18, TypeScript, Tailwind CSS, Vite, React Router

### Structure
```
app-bancario-digital/
  pom.xml
  src/main/java/com/bancodigital/app/  # models, DTOs, repositories, services, controllers
  src/test/java/com/bancodigital/app/  # 40 JUnit tests
  frontend/                            # React + TS + Tailwind (Vite)
```

### How to use

Prerequisites: JDK 21, Maven, Node.js.

#### Backend
```bash
cd app-bancario-digital
./mvnw test        # or: mvn test
```
The in-memory H2 database starts automatically; nothing else to install.

To run the application:
```bash
./mvnw spring-boot:run   # or: mvn spring-boot:run
```
The API starts at `http://localhost:8080`.

#### Frontend
```bash
cd frontend
npm install
npm run dev        # http://localhost:5173 (proxy to API on :8080)
npm run build      # production build to frontend/dist
```

### Main endpoints
- `POST /contas` — create account (`titular`, `saldoInicial`)
- `GET /contas` — list accounts
- `GET /contas/{id}` — get account
- `POST /contas/{id}/depositar` — deposit (`valor`)
- `POST /contas/{id}/sacar` — withdraw (`valor`)
- `POST /transferencias` — transfer (`contaOrigemId`, `contaDestinoId`, `valor`)
- `GET /extrato/{contaId}` — account statement

### Tests
```bash
./mvnw test
```
Result: **40 tests passing, 0 failures** (coverage of accounts, transfers with balance validation, statement, repositories, and controller HTTP integration).

### License
MIT

### Author
Kelvin Oliveira
