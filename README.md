# Sistema de pagamentos simplificado

Implementação do desafio de projeto proposto no repositório: https://github.com/PicPay/picpay-desafio-backend

## Diagrama de classes

```mermaid
classDiagram
    class User {
        - id: String
        - name: String
        - document: String
        - email: String
        - password: String
        - type: UserType
        - balance: BigDecimal
    }

    class UserType {
        <<enumeration>>
        COMMON
        MERCHANT
    }

    class Transaction {
        - id: String
        - payer: User
        - payee: User
        - amount: BigDecimal
        - timestamp: LocalDateTime
    }

    User "1" *-- "0..*" Transaction : has
    User "1" *-- "1" UserType : has

```

## Desenvolvimento

### Testes

Testes unitários e de integração nas camadas de service e controller (com o TestRestTemplate) utilizando JUnit e Mockito

### Documentação

Springdoc-openapi Swagger-ui endpoint:

http://localhost:8080/swagger-ui
