# 🏛️ Backend Engineering & Architecture
> Documentação técnica detalhada das escolhas de engenharia do Industrial Optimizer.

## 1. Stack Tecnológica e Justificativa
* **Java 21 (LTS):** Escolhido para aproveitar os recursos modernos de linguagem e performance, garantindo longevidade ao projeto.
* **Spring Boot 3.3.0:** Utilizado como framework core pela sua robustez em sistemas industriais e facilidade de integração via REST.
* **Spring Data JPA & H2:** Implementados para garantir uma camada de persistência abstrata e rápida (in-memory), facilitando a portabilidade do teste.
* **Lombok:** Aplicado para manter o código limpo (Clean Code) e focar na lógica de negócio, eliminando boilerplate.

## 2. Padrões de Projeto (Design Patterns)
* **Layered Architecture:** O código foi dividido em Controller, Service, Repository e Model para garantir a separação de responsabilidades (Separation of Concerns).
* **Dependency Injection (DI):** Utilizado nativamente pelo Spring para desacoplar as classes, facilitando a testabilidade unitária.
* **Singleton Pattern:** Os Services são instanciados como singletons pelo Spring Container, otimizando o uso de memória.

## 3. Fontes e Referências Teóricas
* **Arquitetura em Camadas:** Baseado nos princípios de *Clean Architecture* de Robert C. Martin (Uncle Bob).
* **Spring Data JPA:** Referência oficial da [Spring.io](https://spring.io/projects/spring-data-jpa).
* **RESTful API:** Princípios definidos por Roy Fielding em sua tese sobre sistemas distribuídos.