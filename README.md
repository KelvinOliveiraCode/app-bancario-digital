# App Bancário Digital

Conta digital full-stack: backend Java 21 + Spring Boot 3 e frontend React + TypeScript + Tailwind. Escopo MVP: criar contas, depositar, sacar, transferir entre contas e consultar extrato.

## Sobre o projeto

O backend expõe uma API REST para criar contas, consultar saldo, depositar, sacar, transferir entre contas (com validação de saldo suficiente) e consultar o extrato. O frontend consome essa API e oferece telas para gerenciar contas, fazer transferências e visualizar o extrato.

O saldo é armazenado com BigDecimal para evitar erros de ponto flutuante em valores monetários. Erros de negócio e de validação são retornados em um corpo padronizado (timestamp, status, erro, mensagem).

## Stack

- Java 21, Spring Boot 3.4.4 (Web, Data JPA, Validation)
- H2 (banco em memória, jdbc:h2:mem:bancodigital, para desenvolvimento e testes)
- Lombok, JUnit 5, AssertJ
- React 18, TypeScript, Tailwind CSS, Vite, React Router

## Estrutura do repositório

```
app-bancario-digital/
  pom.xml
  src/main/java/com/bancodigital/app/  # modelos, DTOs, exceções, repositórios, serviços, controladores
  src/test/java/com/bancodigital/app/  # 40 testes JUnit
  frontend/                            # React + TS + Tailwind (Vite)
```

## Pré-requisitos

- JDK 21
- Maven
- Node.js

## Como usar

### Backend

Execute os testes:

```bash
cd app-bancario-digital
./mvnw test        # ou: mvn test
```

O banco H2 em memória é iniciado automaticamente; não é preciso instalar banco adicional.

Rode a aplicação:

```bash
./mvnw spring-boot:run   # ou: mvn spring-boot:run
```

A API fica disponível em http://localhost:8080.

### Frontend

```bash
cd frontend
npm install
npm run dev        # http://localhost:5173 (proxy /api para a API em :8080)
npm run build      # build de produção em frontend/dist
```

## Endpoints da API

Base: http://localhost:8080

- `POST /contas` — criar conta (`titular`, `saldoInicial`)
- `GET /contas` — listar contas
- `GET /contas/{id}` — buscar conta por ID
- `POST /contas/{id}/depositar` — depósito (`valor`)
- `POST /contas/{id}/sacar` — saque (`valor`)
- `POST /transferencias` — transferência (`contaOrigemId`, `contaDestinoId`, `valor`)
- `GET /extrato/{contaId}` — extrato da conta

Respostas de erro seguem o formato `{ timestamp, status, erro, mensagem }`. Exemplos: conta não encontrada (404), saldo insuficiente (400), dados inválidos (400).

## Testes

```bash
./mvnw test
```

Resultado: 40 testes passando, 0 falhas (contas, transferências com validação de saldo, extrato, repositórios e integração HTTP dos controladores).

## Licença

MIT

## Autor

Kelvin Oliveira
