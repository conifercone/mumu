## Why

Genix 模块的 gRPC 接口实现当前放在 `genix-application` 层，与 IAM 模块重构前的问题相同：`*ServiceImpl` 同时承担业务逻辑和 gRPC 协议适配的双重职责，违反六边形架构中"应用核心不依赖外部技术细节"的原则。IAM 模块已完成同类重构，Genix 应保持一致。

## What Changes

- 在 `genix-adapter` 新建 `grpc/` 包，创建 3 个 gRPC 适配器类：`CaptchaCodeGrpcService`、`CountryGrpcService`、`PrimaryKeyGrpcService`
- 从 `genix-application` 的 `CaptchaCodeServiceImpl`、`CountryServiceImpl`、`PrimaryKeyServiceImpl` 中移除 `@GrpcService`、`extends *ImplBase` 及 gRPC 方法
- BarCodeServiceImpl 和 QRCodeServiceImpl 当前已无 gRPC 代码，无需修改
- 更新 `genix-adapter/build.gradle.kts`：添加 gRPC 和 `mumu-extension` 依赖
- 更新 `genix-application/build.gradle.kts`：移除 `grpc-stub` 和 `spring-grpc-starter`，保留 `grpc-protobuf`

## Capabilities

### New Capabilities

- `genix-grpc-adapter`: Genix 模块的 gRPC 协议适配层，处理 gRPC 请求的协议转换并委托给 application 层

### Modified Capabilities

无——所有 gRPC 接口行为不变，仅调整代码位置。

## Impact

- 新建：`genix-adapter/src/main/java/baby/mumu/genix/adapter/grpc/` 下 3 个类
- 修改：`genix-application/service/` 下 3 个 `*ServiceImpl`，移除 gRPC 代码
- 修改：`genix-adapter/build.gradle.kts`、`genix-application/build.gradle.kts`
- BarCodeServiceImpl、QRCodeServiceImpl 无变化
- 运行时行为完全不变
