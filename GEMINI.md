# GEMINI.md

## Project Overview

**mumu** (沐沐) is a "Delightfully Clean & Ready-to-Go" management system. It is designed with a strong focus on clean code, modern technologies, and a feature-complete microservices architecture.

### Key Technologies
- **Java:** 25 (Adoptium Temurin)
- **Framework:** Spring Boot 4.x, Spring Cloud 2025.x
- **Language:** Hybrid Java/Kotlin
- **Database:** PostgreSQL (IAM), MongoDB (IAM/Storage), Redis (Caching/Rate Limiting), Elasticsearch (Log)
- **Messaging:** Kafka (Log)
- **Service Discovery & Config:** Consul
- **Architecture:** Hexagonal (Ports & Adapters) / Clean Architecture, Domain-Driven Design (DDD)
- **Build System:** Gradle (Kotlin DSL)

### Architecture
The project follows a multi-module microservices structure under `mumu-services`:
- `mumu-iam`: Identity and Access Management (OAuth2, Users, Roles, Permissions).
- `mumu-log`: Centralized logging and query service.
- `mumu-storage`: File storage service.
- `mumu-genix`: Generation and Mix service.

Supporting modules include:
- `mumu-basis`: Common utilities, annotations, and base domain models.
- `mumu-extension`: Function expansion (OCR, Translation, Rate Limiting, etc.).
- `mumu-processor`: Custom annotation processors for code generation (e.g., Metamodel).
- `mumu-benchmark`: JMH-based performance testing.

Internal module structure (Clean Architecture):
- `*-adapter`: Interface with the outside world (Web, gRPC, Persistence).
- `*-application`: Application services and use cases.
- `*-domain`: Core business logic and entities.
- `*-infra`: Technical implementation details (External API clients, specialized repositories).
- `*-client`: Client libraries for other services to consume.

## Building and Running

### Prerequisites
- JDK 25
- Docker (for dependencies like PostgreSQL, Redis, etc.)

### Key Commands
- **Build Project:** `./gradlew build`
- **Run Tests:** `./gradlew test`
- **Clean Build:** `./gradlew clean build`
- **Checkstyle:** `./gradlew checkstyleMain` (or use `scripts/checkstyle.sh`)
- **PMD:** `./gradlew pmdMain` (or use `scripts/pmd.sh`)
- **Install Git Hooks:** `./gradlew installGitHooks`

## Development Conventions

### Coding Style
- **Lombok & JavaPoet:** Heavily used for reducing boilerplate and code generation.
- **Kotlin:** Used for Gradle scripts and specific modules/classes (e.g., in `mumu-basis`).
- **Annotations:** Custom annotations like `@Metamodel`, `@Meta`, and `@RateLimiter` drive logic and code generation.
- **Clean Code:** Prioritize readability, minimal filler, and idiomatic usage of Java 25 features.

### Code Generation
- **Metamodel:** Annotate domain entities with `@Metamodel` to generate metamodel classes (e.g., `User` -> `UserMetamodel`) via `mumu-processor`. This helps with type-safe field referencing.

### Testing
- **JUnit 5:** Primary testing framework.
- **JMH:** Used in `mumu-benchmark` for performance critical components.

### Contribution Guidelines
- **Git Hooks:** Always run `installGitHooks` to ensure commit messages follow project standards.
- **License Headers:** All source files must include the Apache License 2.0 header.
- **Validation:** Ensure Checkstyle and PMD pass before submitting PRs.

### Workflow Constraints
- **No Automatic Commits:** NEVER stage or commit changes unless specifically and explicitly requested by the user. Providing a code fix or implementation does not imply permission to commit.
- **Language Preference:** Always communicate and respond in **Chinese** (中文).
- **Code Comments:** Use **Chinese** (中文) for all code comments and documentation within the source code.

## Key Files & Directories
- `build.gradle.kts`: Root build configuration and global task definitions.
- `gradle/libs.versions.toml`: Centralized dependency management.
- `scripts/`: Maintenance and CI/CD scripts (Checkstyle, PMD, Git Hooks).
- `apis/http-client/`: IntelliJ HTTP Client files for API testing.
