# Maven Dependencies Required for Data Validation & Integrity

Add these dependencies to your `pom.xml` to support Jakarta Validation and error handling.

## Spring Boot 3.x with Jakarta Validation

```xml
<dependencies>
    <!-- Spring Boot Starter Web (includes Spring Security, REST) -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
        <version>3.2.0</version>
    </dependency>

    <!-- Jakarta Bean Validation API (JSR-303 successor) -->
    <!-- Used for: @NotNull, @NotBlank, @Positive, @PositiveOrZero, @Email, etc. -->
    <dependency>
        <groupId>jakarta.validation</groupId>
        <artifactId>jakarta.validation-api</artifactId>
        <version>3.0.2</version>
    </dependency>

    <!-- Hibernate Validator (default JSR-303 implementation for Spring Boot) -->
    <!-- Provides the actual validation engine behind the annotations -->
    <dependency>
        <groupId>org.hibernate.validator</groupId>
        <artifactId>hibernate-validator</artifactId>
        <version>8.0.1.Final</version>
    </dependency>

    <!-- Spring Boot Starter Validation (convenience starter) -->
    <!-- Automatically includes jakarta.validation-api and hibernate-validator -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-validation</artifactId>
        <version>3.2.0</version>
    </dependency>

    <!-- Spring Boot JPA/Data (for database operations) -->
    <!-- Used for: @Entity, @Repository, JpaRepository, etc. -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
        <version>3.2.0</version>
    </dependency>

    <!-- Database Driver (example: H2 for development, MySQL for production) -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <version>2.2.220</version>
        <scope>runtime</scope>
    </dependency>

    <!-- Lombok (for @Data, @Builder, @Slf4j annotations) -->
    <dependency>
        <groupId>org.projectlombok</groupId>
        <artifactId>lombok</artifactId>
        <version>1.18.30</version>
        <scope>provided</scope>
    </dependency>

    <!-- SLF4J (Logging) -->
    <!-- Already included by spring-boot-starter-web, but explicit for clarity -->
    <dependency>
        <groupId>org.slf4j</groupId>
        <artifactId>slf4j-api</artifactId>
        <version>2.0.7</version>
    </dependency>

    <!-- Logback (SLF4J Implementation) -->
    <dependency>
        <groupId>ch.qos.logback</groupId>
        <artifactId>logback-classic</artifactId>
        <version>1.4.11</version>
    </dependency>

    <!-- Jackson (for JSON serialization/deserialization) -->
    <!-- Already included by spring-boot-starter-web, but explicit for clarity -->
    <dependency>
        <groupId>com.fasterxml.jackson.core</groupId>
        <artifactId>jackson-databind</artifactId>
        <version>2.16.1</version>
    </dependency>

    <!-- Testing Dependencies -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <version>3.2.0</version>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter-api</artifactId>
        <version>5.9.3</version>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>5.5.1</version>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-junit-jupiter</artifactId>
        <version>5.5.1</version>
        <scope>test</scope>
    </dependency>
</dependencies>

<properties>
    <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
    <maven.compiler.source>17</maven.compiler.source>
    <maven.compiler.target>17</maven.compiler.target>
</properties>
```

## Minimal Setup (Spring Boot 3.x)

If you want the bare minimum for validation, use Spring Boot's starter:

```xml
<!-- This single dependency brings everything you need -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
    <version>3.2.0</version>
</dependency>

<!-- Plus the web starter for REST endpoints -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
    <version>3.2.0</version>
</dependency>

<!-- Plus data-jpa for database operations -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
    <version>3.2.0</version>
</dependency>
```

## Key Jakarta Validation Annotations

| Annotation | Purpose | Applies To |
|-----------|---------|-----------|
| `@NotNull` | Value must not be null | Any |
| `@NotBlank` | String not null/empty/whitespace | String |
| `@NotEmpty` | Collection/array not empty | Collection |
| `@Positive` | Value > 0 (strictly greater) | Number |
| `@PositiveOrZero` | Value >= 0 | Number |
| `@Negative` | Value < 0 | Number |
| `@Email` | Valid email format | String |
| `@Size(min=, max=)` | Collection size range | Collection/String |
| `@Min(value)` | Minimum numeric value | Number |
| `@Max(value)` | Maximum numeric value | Number |
| `@DecimalMin`, `@DecimalMax` | BigDecimal min/max | BigDecimal |
| `@Pattern(regexp)` | Regex pattern match | String |
| `@Past` | Date must be in the past | Date |
| `@Future` | Date must be in the future | Date |

## Version Compatibility

| Java Version | Jakarta Validation | Hibernate Validator | Spring Boot |
|------------|------------------|-------------------|------------|
| 17+ | 3.0.x | 8.0.x | 3.2.x |
| 11+ | 2.0.x | 7.0.x | 2.7.x |
| 8+ | 2.0.x (Java EE), 1.1.x (J2EE) | 6.2.x | 2.5.x |

## Configuration in application.properties (Optional)

```properties
# Validation message source
spring.validation.enabled=true

# Locale for validation messages
spring.mvc.locale=pt_BR

# Custom validation messages file
spring.messages.basename=messages
```

## Custom Validation Messages (messages.properties)

```properties
jakarta.validation.constraints.NotNull.message=O campo {0} não pode ser nulo
jakarta.validation.constraints.NotBlank.message=O campo {0} não pode estar vazio
jakarta.validation.constraints.Positive.message={0} deve ser maior que zero
jakarta.validation.constraints.PositiveOrZero.message={0} não pode ser negativo
```

## Verify Installation

After adding dependencies, verify in your IDE:
1. Maven > Clean and Install
2. Check External Libraries for `jakarta.validation-api`
3. Check External Libraries for `hibernate-validator`
4. Rebuild project

## Troubleshooting

**Issue:** "@Valid annotation not recognized"
```
Solution: Ensure spring-boot-starter-validation is in pom.xml
```

**Issue:** "Validation annotations not working"
```
Solution: Add @RestControllerAdvice for exception handling
```

**Issue:** "Custom validation messages not showing"
```
Solution: Implement custom message interpolation in GlobalExceptionHandler
```

---

**End of Dependencies Guide**
