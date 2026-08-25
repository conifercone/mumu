# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build & Development Commands

- `./gradlew build` — compile/package all modules
- `./gradlew test` — run unit tests
- `./gradlew check` — run tests + Checkstyle + PMD
- `./gradlew :mumu-services:mumu-iam:test` — run tests for a single module
- `./gradlew checkstyleMain` — Checkstyle only
- `./gradlew pmdMain` — PMD only
- `./gradlew installGitHooks` — install commit-msg hook
- `./gradlew clean build` — clean build

## Architecture

**Hexagonal Architecture (Ports & Adapters) / Clean Architecture + DDD.** Each service under `mumu-services/` is decomposed into five sub-modules with strict dependency direction:

```
adapter → application → domain ← infra
                  client (consumed by all)
```

- **`*-client`**: API contracts — interfaces, `*Cmd` command objects, `*DTO` data transfer objects, gRPC `*.proto` definitions. Other services depend on this to consume the service.
- **`*-adapter`**: Inbound adapters — REST controllers (`@RestController`), gRPC service implementations (`@GrpcService`).
- **`*-application`**: Application services (`*ServiceImpl`) and use-case executors (`*CmdExe`). Depends on `*-client` and `*-domain`.
- **`*-domain`**: Core entities and gateway (port) interfaces (`*Gateway`). **No infrastructure dependencies.**
- **`*-infra`**: Gateway implementations, JPA/MongoDB repositories, Redis cache adapters, MapStruct convertors/mappers. Depends on `*-domain` and `*-client`.

### Service Modules

- `mumu-iam` — Identity & Access Management (OAuth2, Users, Roles, Permissions)
- `mumu-log` — Centralized logging (Kafka → Elasticsearch)
- `mumu-storage` — File storage (MinIO/S3)
- `mumu-genix` — Generation & Mix service

### Supporting Modules

- `mumu-basis` — Common utilities, annotations, base domain models, enums, POs (foundation for all modules)
- `mumu-extension` — OCR, Translation, Rate Limiting, SQL filter, Face Detection
- `mumu-processor` — Custom annotation processor (Metamodel generator via JavaPoet)
- `mumu-benchmark` — JMH performance testing

## New Feature Flow

1. Add `*Cmd`/`*DTO` in `*-client`
2. Implement `*CmdExe` in `*-application`
3. Define `*Gateway` interface in `*-domain`, implement in `*-infra`
4. Expose REST/gRPC in `*-adapter`
5. Add tests under `src/test/java` mirroring package names

## Key Conventions

### Language & Communication
- Code comments in **Chinese**
- Ensure terminal uses UTF-8 encoding

### Coding Style
- 4 spaces for `.java`, `.kt`, `.groovy`, `.xml`; 2 spaces for `.toml`
- LF line endings, UTF-8, trim trailing whitespace, final newline
- Lombok: `config.stopBubbling = true`, `equalsAndHashCode.callSuper = call`, `toString.callSuper = call`
- MapStruct: `unmappedTargetPolicy = IGNORE`
- Apache License 2.0 headers on all source files

### Key Custom Annotations & Base Classes
- `@Metamodel` + `@Meta` → generates type-safe metamodel classes (e.g., `User` → `UserMetamodel`)
- `@RateLimiter` / `@RateLimiters` → rate limiting (Bucket4j + Redis/Lettuce)
- `@DangerousOperation` → marks destructive operations, invalidates related caches
- Base classes: `DomainModel`, `DataTransferObject`, `PersistentObject`, `JpaBasisDefaultPersistentObject`, `ResponseWrapper<T>`
- MapStruct base: `BaseMapper`, `GrpcMapper`

### Controller Patterns
- `@RestController`, `@Validated`, `@RequestMapping`, `@Tag`, per-endpoint `@Operation`, `@API(since=...)`, `@RateLimiter`
- `@RequestBody` + `@Validated` for commands, `@ModelAttribute` for query commands, `@PathVariable` for IDs
- `ResponseWrapper<T>` for responses; `Page` for count-based pagination, `Slice` for no-count

### Database Migrations
- Flyway migrations in `src/main/resources/db/migration/postgresql/`
- Naming: `Vx.y.z__short_description.sql`

### gRPC
- Proto files in `*-client/src/main/proto/`
- Services extend `*ImplBase` and use `@GrpcService`

## Commit Convention

Format: `<type>(<scope>): <subject>` (max 50 chars subject); subject/body in **Chinese**, scope in **English**

Types: `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `build`, `ci`, `chore`, `revert`

## Important Rules

- `mumu-processor` generates code at compile time — if you see missing `*Metamodel` classes, run `./gradlew build` first
- Dependencies are managed via Gradle Version Catalog (`gradle/libs.versions.toml`) — add/update dependencies there first

<!-- SPECKIT START -->
For additional context about technologies to be used, project structure,
shell commands, and other important information, read the current plan
<!-- SPECKIT END -->

## Agent skills

### Issue tracker

Issues are tracked as GitHub issues via the `gh` CLI. See `docs/agents/issue-tracker.md`.

### Triage labels

The five canonical triage labels are used as-is. See `docs/agents/triage-labels.md`.

### Domain docs

Single-context: `CONTEXT.md` at repo root plus `docs/adr/`. See `docs/agents/domain.md`.
