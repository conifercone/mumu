## ADDED Requirements

### Requirement: genix-application 不依赖 genix-infra
`genix-application` 模块 SHALL NOT 声明对 `genix-infra` 模块的 Gradle 编译依赖，且 SHALL NOT import `baby.mumu.genix.infra` 包下的任何类。

#### Scenario: 验证 gradle 依赖
- **WHEN** 检查 `genix-application/build.gradle.kts` 的 `dependencies` 块
- **THEN** 不存在 `implementation(project(":mumu-services:mumu-genix:genix-infra"))`

#### Scenario: 验证 import 路径
- **WHEN** 搜索 `genix-application/src/` 下的所有 Java 文件
- **THEN** 不存在 `import baby.mumu.genix.infra.*` 语句

### Requirement: Convertor 命名与 iam 一致
genix 的 Convertor 和 Mapper 类 SHALL 遵循与 iam 服务相同的命名约定：application 层使用 `*AssemblerConvertor` + `*AssemblerMapper`，infra 层使用 `*PersistenceConvertor` + `*PersistenceMapper`。

#### Scenario: application 层命名
- **WHEN** 查看 `genix-application/src/.../application/<domain>/convertor/` 目录
- **THEN** 存在 `*AssemblerConvertor.java` 和 `*AssemblerMapper.java`，不存在旧命名的 `CountryConvertor`、`BarCodeConvertor`、`QRCodeConvertor`

#### Scenario: infra 层命名
- **WHEN** 查看 `genix-infra/src/.../infra/captcha/convertor/` 和 `genix-infra/src/.../infra/captcha/mapper/` 目录
- **THEN** 存在 `CaptchaCodePersistenceConvertor.java` 和 `CaptchaCodePersistenceMapper.java`

### Requirement: 对象转换通过 Convertor 包装
外部代码 SHALL NOT 直接调用 MapStruct Mapper 的 `INSTANCE`。所有对象转换 MUST 通过对应的 Convertor 组件进行。

#### Scenario: GatewayImpl 使用 PersistenceConvertor
- **WHEN** 查看 `CaptchaCodeGatewayImpl` 的依赖注入字段
- **THEN** 注入的是 `CaptchaCodePersistenceConvertor`，而非 `CaptchaCodePersistenceMapper.INSTANCE`

#### Scenario: Executor 使用 AssemblerConvertor
- **WHEN** 查看任意 genix application executor 的依赖注入字段
- **THEN** 注入的是对应的 `*AssemblerConvertor`，而非直接引用 `*AssemblerMapper.INSTANCE`
