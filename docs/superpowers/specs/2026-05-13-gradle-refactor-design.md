# Gradle Refactor: Included Build (build-logic) Design Specification

## 1. Overview
This specification details the refactoring of the `mumu` project's Gradle configuration. The goal is to move the monolithic and legacy `subprojects` block from the root `build.gradle.kts` into a modern "Included Build" structure using Convention Plugins.

## 2. Goals
- Resolve IntelliJ IDEA warning: "The ‘apply’ plugin syntax is older and not recommended".
- Improve build maintainability by separating concerns into granular convention plugins.
- Enhance build performance by using Included Build (build-logic).
- Provide type-safe accessors for plugin configurations.

## 3. Architecture: Included Build
We will create a `build-logic` project in the root directory. This project will be included via `settings.gradle.kts`.

### 3.1 Directory Structure
```text
mumu/
├── build-logic/
│   ├── settings.gradle.kts
│   ├── build.gradle.kts
│   └── src/main/kotlin/
│       ├── mumu.java-conventions.gradle.kts
│       ├── mumu.kotlin-conventions.gradle.kts
│       ├── mumu.quality-conventions.gradle.kts
│       ├── mumu.publish-conventions.gradle.kts
│       └── mumu.processor-conventions.gradle.kts
├── gradle/
│   └── libs.versions.toml
├── settings.gradle.kts
└── build.gradle.kts
```

## 4. Convention Plugins Detail

### 4.1 `mumu.java-conventions`
- **Purpose**: Base Java configuration.
- **Logic**:
  - Apply `java-library`.
  - Set `java.toolchain` or `tasks.withType<JavaCompile>` (UTF-8, `-parameters`).
  - Common repositories (`mavenCentral`, Spring Milestone).
  - Common dependencies (BOMs: Spring Boot, Cloud, gRPC, Protobuf, Guava, AWS).
  - Essential libraries (Guava, Commons, Jackson, etc.).

### 4.2 `mumu.kotlin-conventions`
- **Purpose**: Kotlin-specific configuration.
- **Logic**:
  - Apply `org.jetbrains.kotlin.jvm`, `org.jetbrains.kotlin.plugin.spring`, `org.jetbrains.kotlin.plugin.jpa`.
  - Configure `KotlinCompilationTask` (freeCompilerArgs).

### 4.3 `mumu.quality-conventions`
- **Purpose**: Static analysis (Checkstyle, PMD).
- **Logic**:
  - Apply `checkstyle`, `pmd`.
  - Configure tool versions from root project properties.
  - Define `ruleSetFiles` paths.
  - Enable task caching.

### 4.4 `mumu.publish-conventions`
- **Purpose**: Publishing and signing.
- **Logic**:
  - Apply `signing`, `project-report`, `idea`.
  - Define `sourceJar` task.
  - Configure `signing` block with environment variables.
  - Configure `Jar` task manifest attributes.

### 4.5 `mumu.processor-conventions`
- **Purpose**: Special handling for `mumu-processor`.
- **Logic**:
  - Check for `mumu-processor` dependency in `annotationProcessor` configuration.
  - In `compileJava`, add compiler args (`-Agradle.version`, `-Aproject.version`, etc.) if processor is present.

## 5. Migration Steps
1. **Initialize `build-logic`**: Create `settings.gradle.kts` and `build.gradle.kts` inside `build-logic`.
2. **Expose Version Catalog**: Ensure `build-logic` can see `libs.versions.toml`.
3. **Implement Convention Plugins**: Move logic from root `build.gradle.kts` to corresponding `*.gradle.kts` files in `build-logic`.
4. **Update Root `settings.gradle.kts`**: Add `includeBuild("build-logic")`.
5. **Clean Root `build.gradle.kts`**: Remove `subprojects` block.
6. **Apply Plugins to Subprojects**: Update each subproject's `build.gradle.kts` to apply the new convention plugins.

## 6. Success Criteria
- Project builds successfully: `./gradlew clean build`.
- Checkstyle and PMD tasks run as expected.
- Source jars and signing tasks remain functional.
- No "apply plugin" syntax warnings in IDEA for the new structure.
