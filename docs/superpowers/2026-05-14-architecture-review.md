# Mumu 项目架构审查报告

**日期**: 2026-05-14
**分支**: develop

---

## 一、项目总览

| 维度 | 现状 |
|---|---|
| 架构模式 | 六边形架构 (Ports & Adapters) + DDD |
| 构建系统 | Gradle 9.5.0 + Kotlin DSL + Version Catalog |
| 规约插件 | 8 个基础 + 3 个组合 = 11 个 |
| 服务模块 | 4 个 (iam / genix / log / storage) |
| 子模块总数 | 20 个 (每服务 5 层) + 4 个服务聚合 = 24 |
| 支撑模块 | basis / extension / processor / benchmark |
| Java 版本 | 25 |

---

## 二、架构违规

### 2.1 高：genix-application 直接依赖 genix-infra

```
genix-application/build.gradle.kts:13
implementation(project(":mumu-services:mumu-genix:genix-infra"))
```

六边形架构的依赖方向是：

```
adapter -> application -> domain <- infra
```

Application 层绝不能依赖 Infra 层。Application 应该通过 domain 层定义的 Gateway 接口来调用 infra 的能力，而不是直接引用。

对比其他三个服务（iam / log / storage）的 application 层，没有一个依赖 infra。这是 genix 独有的问题，需要将 genix-application 中对 infra 的直接引用改为通过 domain 的 Gateway 接口。

### 2.2 中：Domain 层依赖 Spring 框架

| 模块 | Spring 依赖 |
|---|---|
| iam-domain | spring-security-core + spring-data-commons |
| log-domain | spring-data-commons |
| genix-domain | 无 |
| storage-domain | 无 |

严格 DDD 要求 domain 层是纯 POJO，不依赖任何框架。spring-data-commons 提供 Page/Slice 类型，spring-security-core 提供 GrantedAuthority。虽然这是常见的务实折中，但它们是技术债务——如果将来要迁移框架，domain 层会受影响。genix-domain 和 storage-domain 已经做到了无框架依赖，证明了这是可行的。

### 2.3 中：iam-client 和 storage-client 依赖 domain 模块

| 模块 | 是否依赖 domain | 评价 |
|---|---|---|
| iam-client | 依赖 iam-domain | API 泄露领域类型 |
| genix-client | 不依赖 | 干净 |
| log-client | 不依赖 | 干净 |
| storage-client | 依赖 storage-domain | API 泄露领域类型 |

Client 模块的理想定位是纯 API 契约（DTO、Command、proto 定义），供其他服务消费。当它依赖 domain 时，意味着领域类型通过 client 泄露给了外部消费者。genix-client 和 log-client 证明了不依赖 domain 是可行的。

---

## 三、模块设计问题

### 3.1 高：mumu-extension 是 God Module

一个模块承载了 25+ 个互不相关的子包：

```
annotations      - 自定义注解 (@NotBlankOrNull)
aspects          - AOP 切面 (限流、危险操作)
authentication   - 认证工具
fd/opencv        - 人脸识别 (OpenCV)
filters          - 签名过滤器
grpc/interceptors - gRPC 拦截器
idempotent/redis  - 幂等性 (Redis)
listener         - 事件监听器
mvc/interceptor  - MVC 拦截器
nosql            - NoSQL 工具
ocr/aliyun       - OCR (阿里云)
ocr/tess4j       - OCR (Tess4j)
processor/grpc   - gRPC 响应处理器
provider         - 提供者
rl               - 限流 (Bucket4j)
sql/filter/p6spy - SQL 过滤器 (P6Spy)
translation/aliyun - 翻译 (阿里云)
translation/deepl  - 翻译 (DeepL)
validator        - 校验器
```

人脸识别和 SQL 过滤器没有任何关系，翻译和 gRPC 拦截器也没有。随着功能增长，这个模块会越来越臃肿。建议按领域拆分为独立模块：

- mumu-extension-security（限流、幂等、签名过滤）
- mumu-extension-ocr（OCR）
- mumu-extension-translation（翻译）
- mumu-extension-fd（人脸识别）

### 3.2 中：服务间运行时耦合

```
iam-infra -> log-client + genix-client + storage-client (4 个外部依赖)
log-infra -> genix-client (1 个)
storage-infra -> log-client + genix-client (2 个)
```

虽然 infra 层作为服务编排器调用其他服务是常见模式，但直接 RPC 依赖意味着服务不能独立部署。建议考虑通过领域事件（Kafka）异步解耦那些不需要同步响应的调用。

---

## 四、构建配置问题

### 4.1 中：settings.gradle.kts 大量重复样板

每个子模块需要 2 行 x 20 个子模块 = 40 行重复代码：

```kotlin
include("mumu-services:mumu-iam:iam-adapter")
findProject(":mumu-services:mumu-iam:iam-adapter")?.name = "iam-adapter"
```

可以用 Kotlin 辅助函数消除重复：

```kotlin
fun includeServiceModules(service: String) {
    listOf("adapter", "application", "client", "domain", "infra").forEach { layer ->
        include(":mumu-services:$service:$service-$layer")
        findProject(":mumu-services:$service:$service-$layer")?.name = "$service-$layer"
    }
}
```

### 4.2 低：规约插件设计良好

11 个插件的层次清晰，组合插件有效减少了样板。mumu.springboot-conventions 只在 4 个服务聚合模块中使用，不与 mumu.service-conventions 合并是正确的设计。

### 4.3 低：无循环依赖

经过全量检查，项目中不存在循环依赖。依赖方向基本符合预期。

---

## 五、问题优先级汇总

| 优先级 | 问题 | 影响范围 | 修复难度 |
|---|---|---|---|
| 高 | genix-application 依赖 genix-infra | genix 服务 | 中 |
| 高 | mumu-extension 职责过载 | 全项目 | 高 |
| 中 | iam/log domain 依赖 Spring 框架 | iam / log | 中 |
| 中 | iam/storage client 依赖 domain | iam / storage | 中 |
| 中 | 服务间运行时耦合 | 全项目 | 高 |
| 中 | settings.gradle.kts 样板代码 | 构建配置 | 低 |

---

## 六、亮点

- 六边形架构在 4 个服务中的落地高度一致，20 个子模块结构完全统一
- build-logic 规约插件体系设计优秀，组合插件减少了大量样板
- adapter -> application -> domain 的依赖方向在 3/4 的服务中得到遵守
- Domain 层没有对 infra 的直接依赖，Gateway 接口解耦做得干净
- Version Catalog 统一管理所有外部依赖版本
- PMD + Checkstyle 质量门禁覆盖所有模块
