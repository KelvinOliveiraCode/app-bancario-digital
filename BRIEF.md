# BRIEF — app-bancario-digital

## Objetivo / Goal
Projeto "app-bancario-digital" (Full-Stack): conta digital com Java Spring Boot 3 + React/TS.
Escopo MVP: contas, transferências e extrato.

Digital banking project (Full-Stack): Java Spring Boot 3 + React/TS digital account.
MVP scope: accounts, transfers, statement.

## Critérios de aceitação / Acceptance criteria
- Transferência com validação de saldo (transfer with balance validation)
- Testes que passem (passing tests)
- README completo PT-BR + EN (complete PT-BR + EN README)

## Entregas / Deliverables
1. Backend Java 21 + Spring Boot 3 (`pom.xml`, modelos/DTOs/repositórios/serviços/controladores para Conta, Transferencia e Extrato, tratamento global de exceções)
2. 40 testes JUnit passando via `./mvnw test` (ou `mvn test`)
3. Frontend mínimo em `frontend/` (React + TypeScript + Tailwind + Vite) com build de produção funcionando
4. README.md bilíngue PT-BR + EN (título, descrição, como usar, testes, licença MIT, autor Kelvin Oliveira)
5. BRIEF.md (este arquivo)

## Como verificar / How to verify
```bash
# raiz do projeto / project root
./mvnw test        # (ou mvn test) -> deve reportar 40 testes, 0 falhas, BUILD SUCCESS

# frontend
cd frontend
npm install
npm run build      # tsc + vite build -> deve gerar frontend/dist sem erros
```

## Localização / Location
`C:\Users\Kelvin\AppData\Local\Temp\opencode\github-staging\projects\app-bancario-digital\`

## Autor / Author
Kelvin Oliveira — Licença MIT
