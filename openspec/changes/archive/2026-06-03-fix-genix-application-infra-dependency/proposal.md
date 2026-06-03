## Why

`genix-application` 直接依赖 `genix-infra`，违反了六边形架构的核心约束：application 层只能依赖 domain 层，不能依赖 infra 层。这是四个服务模块中唯一存在该违规的模块（iam、log、storage 均已修正）。

iam 服务在 2026-05-08/09 通过对应 commit 完成了同样的重构，genix 应参照 iam 的既定模式解决。

## What Changes

- 将 `CountryConvertor/CountryMapper`、`BarCodeConvertor/BarCodeMapper`、`QRCodeConvertor/QRCodeMapper` 从 `genix-infra` 移至 `genix-application`，并统一重命名为 `*AssemblerConvertor/*AssemblerMapper`
- 拆分 `CaptchaCodeConvertor/CaptchaCodeMapper`：应用层转换逻辑（Cmd→Entity、Entity→DTO、gRPC 转换）移至 `genix-application` 的 `CaptchaCodeAssemblerConvertor/CaptchaCodeAssemblerMapper`，持久化转换逻辑（Entity→CachePO）保留在 `genix-infra` 的 `CaptchaCodePersistenceConvertor/CaptchaCodePersistenceMapper`
- 从 `genix-application/build.gradle.kts` 中删除 `implementation(project(":mumu-services:mumu-genix:genix-infra"))`
- 更新 12 个 executor/service 文件的 import 路径

## Capabilities

### New Capabilities

- `genix-hexagonal-conformance`: genix 服务的 application 层不再依赖 infra 层，Convertor 命名和包装关系与 iam 保持一致

### Modified Capabilities

<!-- 无现有 spec 需要修改，纯架构重构无行为变更 -->

## Impact

- **genix-application**: 12 个 executor/service 文件 import 路径变更
- **genix-infra**: 删除 8 个 Convertor/Mapper 文件，新建 2 个 Persistence 文件，`CaptchaCodeGatewayImpl` 改用新 Convertor
- **构建**: `genix-application/build.gradle.kts` 删除 1 行 infra 依赖
