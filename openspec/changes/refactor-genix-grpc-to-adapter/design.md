## Context

Genix 模块采用与 IAM 相同的六边形架构（genix-adapter / genix-application / genix-domain / genix-infra / genix-client），但当前 gRPC 服务端实现（`@GrpcService`、`extends *ImplBase`、`StreamObserver`）放在 `genix-application` 层。IAM 模块已通过 `refactor-iam-grpc-to-adapter` 完成了同样的重构，Genix 应遵循相同模式。

现状：
- `genix-adapter` 已存在，已依赖 `genix-application`
- BarCodeServiceImpl、QRCodeServiceImpl 已无 gRPC 代码，无需修改
- CaptchaCodeServiceImpl：3 个 gRPC 方法 + 3 个业务方法混合
- CountryServiceImpl：2 个 gRPC 方法 + 业务方法混合
- PrimaryKeyServiceImpl：1 个 gRPC 方法 + 1 个业务方法混合

## Goals / Non-Goals

**Goals:**
- 将 gRPC 协议适配代码从 `genix-application` 移至 `genix-adapter`
- 移除 `genix-application` 的 gRPC 运行时依赖（`grpc-stub`、`spring-grpc-starter`）
- 保持所有 gRPC 接口行为不变

**Non-Goals:**
- 不修改 BarCodeServiceImpl、QRCodeServiceImpl
- 不修改 proto 定义
- 不修改 gRPC 客户端调用方

## Decisions

1. **与 IAM 保持完全一致的拆分模式**：创建 `genix-adapter/grpc/*GrpcService` 继承 `*ServiceImplBase`，注入 `*Service` 和 `*AssemblerConvertor`，将原 gRPC 方法逐字迁移。

2. **依赖策略**：`genix-adapter` 新增 `grpc-stub`、`spring-grpc-starter`、`mumu-extension`；`genix-application` 移除 `grpc-stub`、`spring-grpc-starter`，保留 `grpc-protobuf`（convertor 需要 Google 通用 protobuf 类型）。

3. **事务注解**：gRPC 适配器不加 `@Transactional`，业务事务由 application 层的 CmdExe 管理。

## Risks / Trade-offs

- 无行为变更风险：纯代码移动，所有方法逻辑逐字复制
- `genix-adapter` 新增对 `genix-application` convertor 的直接依赖，与 IAM 已建立的模式一致
