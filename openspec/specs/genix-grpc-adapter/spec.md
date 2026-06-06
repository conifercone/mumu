## Purpose

定义 Genix 模块 gRPC 协议适配层的架构规范，确保 gRPC 服务端实现位于 adapter 层，application 层保持纯净。

## Requirements

### Requirement: Genix gRPC 适配层

系统 SHALL 在 `genix-adapter` 模块中提供 gRPC 协议适配层，将 gRPC 请求转换为对 application 层业务接口的调用。

#### Scenario: CaptchaCode gRPC 生成验证码

- **WHEN** gRPC 客户端调用 `CaptchaCodeService.generate(CaptchaCodeGeneratedGrpcCmd)`
- **THEN** `CaptchaCodeGrpcService` 将 proto 命令转换为 `CaptchaCodeGeneratedCmd`，委托 `CaptchaCodeService.generate()`，并将结果转换为 `CaptchaCodeGeneratedGrpcDTO` 返回

#### Scenario: CaptchaCode gRPC 校验验证码

- **WHEN** gRPC 客户端调用 `CaptchaCodeService.verify(CaptchaCodeVerifyGrpcCmd)`
- **THEN** `CaptchaCodeGrpcService` 将 proto 命令转换后委托 `CaptchaCodeService.verify()`，返回 `BoolValue`

#### Scenario: CaptchaCode gRPC 删除验证码

- **WHEN** gRPC 客户端调用 `CaptchaCodeService.delete(Int64Value)`
- **THEN** `CaptchaCodeGrpcService` 委托 `CaptchaCodeService.delete()`，返回 `Empty`

#### Scenario: PrimaryKey gRPC 雪花ID生成

- **WHEN** gRPC 客户端调用 `PrimaryKeyService.snowflake(Empty)`
- **THEN** `PrimaryKeyGrpcService` 委托 `PrimaryKeyService.snowflake()`，返回 `SnowflakeResult`

### Requirement: genix-application 移除 gRPC 依赖

`genix-application` 模块 SHALL NOT 包含 gRPC 服务端实现类（`@GrpcService`、`extends *ImplBase`），SHALL NOT 依赖 `grpc-stub` 和 `spring-grpc-starter`。

#### Scenario: CaptchaCodeServiceImpl 无 gRPC 代码

- **WHEN** 检查 `CaptchaCodeServiceImpl` 源码
- **THEN** 该类不包含 `@GrpcService` 注解、不继承 `CaptchaCodeServiceImplBase`、不包含 gRPC 方法

#### Scenario: genix-application gRPC 依赖已移除

- **WHEN** 检查 `genix-application/build.gradle.kts`
- **THEN** 不包含 `grpc-stub` 和 `spring-grpc-starter` 依赖
