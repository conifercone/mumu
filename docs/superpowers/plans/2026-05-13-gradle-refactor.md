# Gradle Refactor: Included Build (build-logic) Implementation Plan

> **For agentic workers:** REQUIRED SUB-SKILL: Use superpowers:subagent-driven-development (recommended) or superpowers:executing-plans to implement this plan task-by-task. Steps use checkbox (`- [ ]`) syntax for tracking.

**Goal:** 将根目录臃肿且陈旧的 `subprojects` 逻辑迁移到现代化的 `build-logic` (Included Build) 约定插件中，提升可维护性和构建性能。

**Architecture:** 使用 Included Build 模式在 `build-logic` 目录下定义多个细粒度的 Kotlin DSL 插件，并在子模块中按需应用。

**Tech Stack:** Gradle Kotlin DSL, Version Catalog, Java 25, Kotlin 2.x

---

### Task 1: 初始化 build-logic 基础结构

**Files:**
- Create: `build-logic/settings.gradle.kts`
- Create: `build-logic/build.gradle.kts`
- Modify: `settings.gradle.kts`

- [ ] **Step 1: 创建 build-logic/settings.gradle.kts 并配置 Version Catalog**

```kotlin
dependencyResolutionManagement {
    versionCatalogs {
        create("libs") {
            from(files("../gradle/libs.versions.toml"))
        }
    }
}

rootProject.name = "build-logic"
```

- [ ] **Step 2: 创建 build-logic/build.gradle.kts 并应用 kotlin-dsl 插件**

```kotlin
plugins {
    `kotlin-dsl`
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.springboot.gradle.plugin)
    implementation(libs.kotlin.gradle.plugin)
    implementation(libs.lombok.gradle.plugin)
}
```

- [ ] **Step 3: 在根项目 settings.gradle.kts 中引入 build-logic**

```kotlin
// 在文件顶部或合适位置添加
includeBuild("build-logic")
```

- [ ] **Step 4: 提交**

---

### Task 2: 实施 mumu.java-conventions 插件

**Files:**
- Create: `build-logic/src/main/kotlin/mumu.java-conventions.gradle.kts`

- [ ] **Step 1: 编写插件逻辑，包含 Java 基础配置和核心依赖**

```kotlin
plugins {
    `java-library`
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-parameters")
}

repositories {
    mavenCentral()
    maven("https://repo.spring.io/milestone")
}

dependencies {
    implementation(platform(rootProject.libs.spring.boot.dependencies))
    annotationProcessor(platform(rootProject.libs.spring.boot.dependencies))
    implementation(platform(rootProject.libs.spring.cloud.dependencies))
    implementation(platform(rootProject.libs.spring.grpc.dependencies))
    implementation(platform(rootProject.libs.grpc.bom))
    implementation(platform(rootProject.libs.protobuf.bom))
    implementation(platform(rootProject.libs.guava.bom))
    implementation(platform(rootProject.libs.awssdk.bom))
    implementation(rootProject.libs.spring.boot.starter)
    implementation(rootProject.libs.spring.boot.starter.log4j2)
    implementation(rootProject.libs.spring.boot.starter.validation)
    implementation(rootProject.libs.disruptor)
    implementation(rootProject.libs.bundles.jackson)
    testImplementation(rootProject.libs.bundles.jackson)
    implementation(rootProject.libs.jspecify)
    implementation(rootProject.libs.apiguardian.api)
    implementation(rootProject.libs.guava)
    implementation(rootProject.libs.commons.lang3)
    implementation(rootProject.libs.commons.text)
    implementation(rootProject.libs.commons.io)
    implementation(rootProject.libs.commons.collections4)
    implementation(rootProject.libs.jackson.module.kotlin)
    implementation(rootProject.libs.kotlin.reflect)
    implementation(rootProject.libs.progressbar)
    implementation(rootProject.libs.swagger.annotations.jakarta)
    implementation(rootProject.libs.moneta)
    implementation(rootProject.libs.jackson.datatype.moneta)
    testImplementation(rootProject.libs.junit.jupiter)
    testRuntimeOnly(rootProject.libs.junit.platform.launcher)
    annotationProcessor(rootProject.libs.spring.boot.configuration.processor)
    implementation(rootProject.libs.mapstruct)
    annotationProcessor(rootProject.libs.mapstruct.processor)
    testAnnotationProcessor(rootProject.libs.mapstruct.processor)
}
```

- [ ] **Step 2: 提交**

---

### Task 3: 实施质量检查和发布插件

**Files:**
- Create: `build-logic/src/main/kotlin/mumu.quality-conventions.gradle.kts`
- Create: `build-logic/src/main/kotlin/mumu.publish-conventions.gradle.kts`

- [ ] **Step 1: 编写 quality 插件配置 (Checkstyle/PMD)**
- [ ] **Step 2: 编写 publish 插件配置 (Signing/Manifest/SourceJar)**
- [ ] **Step 3: 提交**

---

### Task 4: 实施 Kotlin 和 Processor 插件

**Files:**
- Create: `build-logic/src/main/kotlin/mumu.kotlin-conventions.gradle.kts`
- Create: `build-logic/src/main/kotlin/mumu.processor-conventions.gradle.kts`

- [ ] **Step 1: 迁移 Kotlin 编译器配置和 Spring/JPA 插件**
- [ ] **Step 2: 迁移 mumu-processor 的注解处理器逻辑**
- [ ] **Step 3: 提交**

---

### Task 5: 清理根目录并应用新插件

**Files:**
- Modify: `build.gradle.kts`
- Modify: 所有子项目的 `build.gradle.kts`

- [ ] **Step 1: 删除根目录中的 subprojects 块**
- [ ] **Step 2: 在子模块中应用新插件**
- [ ] **Step 3: 运行测试并验证构建**
Run: `./gradlew clean build`
- [ ] **Step 4: 提交**
