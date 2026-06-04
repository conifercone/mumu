# Repository Guidelines

## Project Structure & Module Organization

- Multi-module Gradle build; primary modules live at repo root (`build-logic/`, `mumu-services/`, `mumu-basis/`, `mumu-extension/`, `mumu-processor/`, `mumu-benchmark/`).
- Standard layout per module: `src/main/java` (and `src/main/kotlin`), `src/test/java` for tests, `src/main/resources` for config and migrations.
- Build conventions live in `build-logic/` (Java, Kotlin, Processor, Protobuf, quality check, and publishing convention plugins); version catalog in `gradle/libs.versions.toml`; static analysis config in `config/`.
- Dependencies are managed via Gradle Version Catalogs in `gradle/libs.versions.toml` (see `https://docs.gradle.org/current/userguide/version_catalogs.html`); add or update dependencies there first.

## Architecture Overview

- Service-first design: each service module owns its API, application logic, domain model, and infrastructure adapters.
- Modules under `mumu-services/` follow the Hexagonal Architecture pattern. Dependency direction flows inward: adapter → application → domain; infra implements gateway interfaces defined in domain. **The application layer MUST NOT depend on the infra layer**.
- Database changes are tracked via migrations under `src/main/resources/db/migration`.

## Hexagonal Architecture Conventions

- Submodule breakdown:
  - `*-adapter`: Web adapters (REST controllers). Depends on application.
  - `*-client`: API interfaces, `*Cmd` command objects, `*DTO` transfer objects, gRPC stubs and `*.proto` definitions. Both adapter and application may depend on it.
  - `*-application`: Service implementations and `*CmdExe` executors. **MUST only depend on client, domain, and external client APIs (e.g. genix-client). MUST NOT depend on infra**.
  - `*-domain`: Domain entities and `*Gateway` interfaces. MUST NOT depend on any other submodule.
  - `*-infra`: Gateway implementations, repositories, PO, convertors/mappers. Depends on domain and client; implements domain gateway interfaces.
- Gradle dependency rules:
  - `*-application/build.gradle.kts` **MUST NOT contain** `implementation(project(":mumu-services:mumu-*:*-infra"))`.
  - At runtime the root boot module assembles all submodules; infra implementation classes are injected into the application layer by Spring.
- Convertor/Mapper naming and responsibility:
  - Application layer: `*AssemblerMapper` (MapStruct interface) + `*AssemblerConvertor` (hand-written wrapper). Responsible for Cmd→Entity, Entity→DTO, and gRPC conversions. **MUST NOT reference PO types**.
  - Infra layer: `*PersistenceMapper` (MapStruct interface) + `*PersistenceConvertor` (hand-written wrapper). **Solely responsible for Entity↔PO conversions**. Different persistence channels (Kafka, ES, etc.) each have their own PersistenceMapper/Convertor.
  - Legacy hybrid Convertor/Mapper classes (handling both Cmd/DTO/gRPC and PO mappings in the same file) should be split and migrated.
- Kafka Consumer rules:
  - Consumers live in the application layer but **MUST NOT directly import Kafka PO types** (e.g. `*KafkaPO`).
  - Consumers receive messages through domain gateway interfaces; PO deserialization is encapsulated in the infra layer via a `saveFromKafkaMessage(String)` method.
  - Kafka topic name constants are defined in the `*-client` module. Consumers reference them through the client layer; they MUST NOT reference them through infra-layer `*Properties` classes.
- `@ConfigurationProperties` classes live in `*-infra` (binding infrastructure config is the infra layer's responsibility), but static constants for topic/index names should be moved to `*-client`.
- When creating a new service module, use genix (refactored), storage (refactored), and log (refactored) as reference templates.

## IAM Service Architecture (Example: mumu-services/mumu-iam)

- Entry point: `IAMApplication` enables JPA/Mongo/Redis, auditing, method security, and transaction management.
- Layers:
  - `iam-adapter`: REST controllers (web adapters).
  - `iam-client`: API interfaces, `*Cmd` and `*DTO`, gRPC stubs and `*.proto` definitions.
  - `iam-application`: service implementations and `*CmdExe` executors.
  - `iam-domain`: entities and gateway interfaces.
  - `iam-infra`: gateway implementations, repositories, cache adapters, and convertors/mappers.
- Persistence: PostgreSQL migrations under `mumu-services/mumu-iam/src/main/resources/db/migration/postgresql`.

## Build, Test, and Development Commands

- `./gradlew build` to compile/package all modules.
- `./gradlew test` to run unit tests.
- `./gradlew check` to run tests plus Checkstyle and PMD.

## Coding Style & Naming Conventions

- Indentation: 4 spaces for `*.java`, `*.kt`, `*.groovy`, `*.xml`; 2 spaces for `*.toml`.
- LF line endings, UTF-8, trim trailing whitespace, and final newline required.

## Testing Guidelines

- JUnit 5 with `useJUnitPlatform()`; tests live under `src/test/java` with `*Test` suffix.
- Run a focused module test with `./gradlew :mumu-iam:test` (or another module).

## IAM Development Conventions (Based on PermissionController + RoleServiceImpl)

- Controllers: use `@RestController`, `@Validated`, `@RequestMapping`, `@Tag`, and per-endpoint `@Operation`, `@API(since=...)`, `@RateLimiter`.
- Requests: `@RequestBody` + `@Validated` for commands, `@ModelAttribute` for query commands, `@PathVariable` for IDs.
- Responses: use `ResponseWrapper<T>` where established; `Page` for count-based pagination, `Slice` for no-count.
- Application layer: implement `*ServiceImpl` that delegates to `*CmdExe` executors; annotate with `@Transactional` and `@Observed` as needed; gRPC services extend `*ImplBase` and use `@GrpcService`.
- Domain/infra boundary: define `*Gateway` interfaces in domain; implement in infra using repositories and cache adapters. Object mapping uses `*AssemblerMapper`/`*AssemblerConvertor` in the application layer (Cmd→Entity, Entity→DTO, gRPC) and `*PersistenceMapper`/`*PersistenceConvertor` in the infra layer (Entity↔PO). See Hexagonal Architecture Conventions above.
- Destructive operations: use `@DangerousOperation` and invalidate related caches.
- Schema changes: add a migration in `db/migration/postgresql` named like `Vx.y.z__short_description.sql`.
- New feature flow: add `*Cmd`/`*DTO` in `iam-client` → implement `*CmdExe` in `iam-application` → add/extend `*Gateway` in `iam-domain` and implement in `iam-infra` → expose REST/gRPC in `iam-adapter`/`*ServiceImpl` → add tests under `mumu-services/mumu-iam/src/test/java` mirroring package names.

## Commit & Pull Request Guidelines

- Commit format: `<type>(<scope>): <subject>` with optional body/footer; keep subjects <= 50 chars.
- PRs should include a clear description, linked issues, and screenshots/logs when behavior changes.

## Security & Configuration Tips

- Report vulnerabilities via GitHub Security Advisories (see `SECURITY.adoc`).
