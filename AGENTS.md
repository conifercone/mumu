# Repository Guidelines

## Project Structure & Module Organization

- Multi-module Gradle build with primary modules at the repo root: `mumu-services/` (submodules `mumu-iam`, `mumu-log`, `mumu-storage`, `mumu-genix`), `mumu-basis/`, `mumu-extension/`, `mumu-processor/`, and `mumu-benchmark/`.
- Standard source layout per module: `src/main/java` (and `src/main/kotlin` where applicable), `src/test/java` for tests.
- Build tooling and rules live in `build.gradle.kts`, version catalogs in `gradle/libs.versions.toml`, and static analysis config under `config/`.
- Utility scripts and git hooks are in `scripts/` (see `scripts/git/hooks`).

## Build, Test, and Development Commands

- `./gradlew build` — compile and package all modules.
- `./gradlew test` — run unit tests across all modules (JUnit 5).
- `./gradlew check` — run tests plus Checkstyle and PMD rules.
- `./gradlew installGitHooks` — install repo git hooks (uses `scripts/git/hooks`).

## Coding Style & Naming Conventions

- Indentation: 4 spaces for `*.java`, `*.kt`, `*.groovy`, `*.xml`; 2 spaces for `*.toml` (see `.editorconfig`).
- Line endings: LF; UTF-8 encoding; trim trailing whitespace; final newline required.
- Java/Kotlin style is enforced by Checkstyle and PMD configured in `build.gradle.kts` with rules in `config/`.

## Testing Guidelines

- Framework: JUnit 5 (`useJUnitPlatform()` in Gradle).
- Test classes follow the `*Test` suffix convention and live under `src/test/java`.
- Run a focused module test with `./gradlew :mumu-basis:test` (replace module as needed).

## Commit & Pull Request Guidelines

- Commit messages follow: `<type>(<scope>): <subject>` with optional body/footer. Example: `feat(iam): Add role search endpoint`.
- Allowed types include: `feat`, `fix`, `docs`, `style`, `refactor`, `perf`, `test`, `build`, `ci`, `chore`, `revert`.
- Keep subjects <= 50 chars; wrap body lines at ~72 chars; use `BREAKING CHANGE:` in the footer when applicable.
- PRs should include a clear description, link relevant issues, and screenshots or logs when changing UI or behavior.

## Security & Configuration Tips

- Report vulnerabilities via GitHub Security Advisories (see `SECURITY.adoc`).
- Some builds require signing environment variables; follow `build.gradle.kts` guidance if publishing artifacts.