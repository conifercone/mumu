<!--
  Sync Impact Report
  ==================
  Version change: 0.0.0 (template placeholder) → 1.0.1
    - 1.0.0: initial ratification — 7 principles + 3 sections + governance
    - 1.0.1: added Python 环境 subsection under 开发工作流
    - 1.0.2: added Java 环境 subsection under 开发工作流
  Modified principles: N/A (initial version — all principles newly defined)
  Added sections:
    - Core Principles (7 principles)
    - 安全与操作约束 (Security & Operational Constraints)
    - 开发工作流 (Development Workflow, 含 Python 环境子章节)
    - Governance
  Removed sections: None
  Templates requiring updates:
    - .specify/templates/plan-template.md: ✅ aligned (Constitution Check section exists, gates will be derived)
    - .specify/templates/spec-template.md: ✅ aligned (no constitution-specific references)
    - .specify/templates/tasks-template.md: ✅ aligned (no constitution-specific references)
  Follow-up TODOs: None
-->

# Mumu 项目宪法

## 核心原则

### I. 六边形架构 + DDD（不可妥协）

所有服务模块 MUST 遵循六边形架构（端口与适配器）结合领域驱动设计的分层方式。每个
`mumu-services/` 下的服务 MUST 拆分为五个子模块，依赖方向严格为：

```
adapter → application → domain ← infra
                  client（所有模块共享）
```

- **`*-client`**：API 契约 — 接口、`*Cmd` 命令对象、`*DTO` 数据传输对象、gRPC
  `*.proto` 定义。其他服务通过依赖本模块来消费该服务。
- **`*-adapter`**：入站适配器 — REST 控制器（`@RestController`）、gRPC
  服务实现（`@GrpcService`）。
- **`*-application`**：应用服务（`*ServiceImpl`）和用例执行器（`*CmdExe`）。
  依赖 `*-client` 和 `*-domain`。
- **`*-domain`**：核心实体与网关（端口）接口（`*Gateway`）。**MUST NOT 包含任何
  基础设施依赖。**
- **`*-infra`**：网关实现、JPA/MongoDB 仓库、Redis 缓存适配器、MapStruct
  转换器/映射器。依赖 `*-domain` 和 `*-client`。

**理由**：六边形架构确保业务逻辑与基础设施解耦，使各层可独立测试和替换；DDD
让领域模型准确反映业务语义，降低沟通成本。

### II. 中文优先

以下场景 MUST 使用中文：

- 所有与开发人员的交互沟通
- 代码注释
- Git 提交消息的 subject 和 body
- Git 提交消息的 scope 使用英文

终端环境 MUST 使用 UTF-8 编码。

**理由**：团队成员以中文为母语，中文注释和沟通提升信息传递效率和准确性。

### III. 禁止猜测

如果任何信息存在不确定性（参数值、配置项、接口行为、业务逻辑等），MUST NOT
猜测或编造。MUST 明确告知开发人员当前信息不足或存在歧义，由开发人员确认后再继续。

**理由**：猜测行为是生产 Bug 的主要来源之一。明确标记不确定性比返回看似正确的结果更有价值。

### IV. 代码风格一致性

在编写代码前 MUST 先阅读项目现有的相关文件，保持风格一致。MUST 优先使用项目已有
的工具和库，不引入新的依赖。

编码规范：

- 缩进：`.java`、`.kt`、`.groovy`、`.xml` 使用 4 空格；`.toml` 使用 2 空格
- 换行符：LF，UTF-8 编码，删除行尾空格，文件末尾保留一个空行
- 所有源文件 MUST 包含 Apache License 2.0 头
- Lombok：`config.stopBubbling = true`，`equalsAndHashCode.callSuper = call`，
  `toString.callSuper = call`
- MapStruct：`unmappedTargetPolicy = IGNORE`

**理由**：一致的代码风格降低认知负担，使代码审查聚焦于逻辑而非格式。

### V. 禁止自动提交

MUST NOT 在没有明确指令的情况下执行 git commit/push 操作。只有开发人员明确说出
"提交"、"commit"、"push" 等指令时才能执行。

**理由**：代码提交是不可逆的操作，必须由开发人员掌控时机和范围。

### VI. 新功能开发流程

新功能的实现 MUST 遵循以下顺序：

1. 在 `*-client` 中添加 `*Cmd`/`*DTO`
2. 在 `*-application` 中实现 `*CmdExe`
3. 在 `*-domain` 中定义 `*Gateway` 接口，在 `*-infra` 中实现
4. 在 `*-adapter` 中暴露 REST/gRPC 端点
5. 在 `src/test/java` 下按包名镜像结构添加测试

Controller 规范：

- 使用 `@RestController`、`@Validated`、`@RequestMapping`、`@Tag`
- 每个端点标注 `@Operation`、`@API(since=...)`、`@RateLimiter`
- 命令使用 `@RequestBody` + `@Validated`，查询使用 `@ModelAttribute`，ID 使用
  `@PathVariable`
- 响应统一使用 `ResponseWrapper<T>` 包装；计数分页使用 `Page`，无计数分页使用
  `Slice`

**理由**：统一的开发流程保证每个功能都经过完整的架构分层，避免跨层调用和技术债务。

### VII. 依赖管理

所有依赖 MUST 通过 Gradle Version Catalog（`gradle/libs.versions.toml`）管理。
添加或更新依赖 MUST 先修改 Version Catalog，再在模块 build 文件中引用。

`mumu-processor` 在编译期生成代码（如 `*Metamodel` 类）— 如遇到缺失的
`*Metamodel` 类，先运行 `./gradlew build`。

**理由**：集中式依赖管理避免版本冲突，确保所有模块使用一致的依赖版本。

## 安全与操作约束

- 破坏性操作 MUST 使用 `@DangerousOperation` 注解标记，自动触发相关缓存失效
- 限流保护：接口 MUST 按需使用 `@RateLimiter` / `@RateLimiters` 注解
- 数据库迁移 MUST 通过 Flyway 管理，迁移脚本放置在
  `src/main/resources/db/migration/postgresql/`，命名格式：`Vx.y.z__简短描述.sql`
- gRPC 服务：Proto 文件放置在 `*-client/src/main/proto/`，服务实现继承
  `*ImplBase` 并使用 `@GrpcService`

## 开发工作流

### 构建与测试

| 命令 | 用途 |
|------|------|
| `./gradlew build` | 编译/打包所有模块 |
| `./gradlew test` | 运行单元测试 |
| `./gradlew check` | 运行测试 + Checkstyle + PMD |
| `./gradlew :mumu-services:mumu-iam:test` | 运行单个模块的测试 |
| `./gradlew checkstyleMain` | 仅 Checkstyle |
| `./gradlew pmdMain` | 仅 PMD |
| `./gradlew installGitHooks` | 安装 commit-msg 钩子 |
| `./gradlew clean build` | 清理后构建 |

### 提交规范

格式：`<type>(<scope>): <subject>`（subject 最多 50 字符）

类型：`feat`、`fix`、`docs`、`style`、`refactor`、`perf`、`test`、`build`、
`ci`、`chore`、`revert`

### 代码搜索

当仓库中存在 `.codegraph/` 目录时，MUST 优先使用 CodeGraph（`codegraph_explore`
MCP 工具或 `codegraph explore` 命令）进行代码定位和理解，而非 grep/find 或逐文件
阅读。

### Python 环境

需要使用 Python 时 MUST 遵循以下约束：

- 默认使用本地 uv 安装的 Python 3.13
- 命令行直接使用 `python3.13` 命令，MUST NOT 使用系统自带的 `python3`
- 如果项目存在虚拟环境（`.venv`），MUST 优先使用虚拟环境中的 Python

**理由**：统一 Python 版本和运行方式避免因环境差异导致的不可重现问题。uv
提供可靠的 Python 版本管理，`.venv` 确保依赖隔离。

### Java 环境

项目 MUST 使用 Java 25，通过 Gradle Toolchain 统一管理 JDK 版本。

配置位置：`build-logic/src/main/kotlin/mumu.base-conventions.gradle.kts`

```kotlin
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}
```

**理由**：Gradle Toolchain 确保所有模块使用一致的 JDK 版本，避免因 JDK
差异导致的编译或运行时问题。

## Governance

本宪法是 Mumu 项目所有开发活动的最高准则，任何实践和决策 MUST 遵循以上原则。

- **修订流程**：宪法的任何修订 MUST 经过文档记录、开发负责人审批，并在
  `.specify/memory/constitution.md` 中更新版本号和修订日期。
- **版本策略**：遵循语义化版本 — MAJOR（原则删除或重新定义）、MINOR（新增原则或
  章节）、PATCH（措辞优化、澄清、格式修正）。
- **合规审查**：每个功能的实现计划（plan.md）MUST 包含 Constitution Check
  章节，逐条验证与宪法的一致性。
- **运行时指引**：项目根目录下的 `CLAUDE.md` 和用户全局 `~/.claude/CLAUDE.md`
  提供运行时级别的开发指引，其内容 MUST 与本宪法保持一致。如有冲突，以本宪法为准。

**Version**: 1.0.2 | **Ratified**: 2026-07-03 | **Last Amended**: 2026-07-03
