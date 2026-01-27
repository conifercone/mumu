# Repository Guidelines

## Project Structure & Module Organization

- Multi-module Gradle build; primary modules live at repo root (`mumu-services/`, `mumu-basis/`, `mumu-extension/`, `mumu-processor/`, `mumu-benchmark/`).
- Standard layout per module: `src/main/java` (and `src/main/kotlin`), `src/test/java` for tests, `src/main/resources` for config and migrations.
- Build rules in `build.gradle.kts`; version catalog in `gradle/libs.versions.toml`; static analysis config in `config/`.
- Dependencies are managed via Gradle Version Catalogs in `gradle/libs.versions.toml` (see `https://docs.gradle.org/current/userguide/version_catalogs.html`); add or update dependencies there first.

## Architecture Overview

- Service-first design: each service module owns its API, application logic, domain model, and infrastructure adapters.
- Database changes are tracked via migrations under `src/main/resources/db/migration`.

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
- Domain/infra boundary: define `*Gateway` interfaces in domain; implement in infra using repositories, cache adapters, and convertors/mappers.
- Destructive operations: use `@DangerousOperation` and invalidate related caches.
- Schema changes: add a migration in `db/migration/postgresql` named like `Vx.y.z__short_description.sql`.
- New feature flow: add `*Cmd`/`*DTO` in `iam-client` → implement `*CmdExe` in `iam-application` → add/extend `*Gateway` in `iam-domain` and implement in `iam-infra` → expose REST/gRPC in `iam-adapter`/`*ServiceImpl` → add tests under `mumu-services/mumu-iam/src/test/java` mirroring package names.

## Commit & Pull Request Guidelines

- Commit format: `<type>(<scope>): <subject>` with optional body/footer; keep subjects <= 50 chars.
- PRs should include a clear description, linked issues, and screenshots/logs when behavior changes.

## Security & Configuration Tips

- Report vulnerabilities via GitHub Security Advisories (see `SECURITY.adoc`).
