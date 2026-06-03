## Context

`genix-application` 是四个服务模块中唯一违反六边形架构依赖规则的模块。其他三个服务（iam、log、storage）的 application 层均不依赖 infra 层。

iam 服务在 2026-05-08/09 完成了同样的重构，建立了以下既定模式：

- **Application 层**: `*AssemblerConvertor` + `*AssemblerMapper`（同包 `convertor/`），负责 Cmd↔Entity、Entity↔DTO、gRPC 转换
- **Infra 层**: `*PersistenceConvertor` + `*PersistenceMapper`（Convertor 在 `convertor/`，Mapper 在 `mapper/`），负责 Entity↔PO/CachePO 持久化转换
- 外部代码永远通过 Convertor 调用，不直接引用 Mapper.INSTANCE

genix 当前有 4 组 Convertor+Mapper 全部放在 `genix-infra`，application 层直接 import：

| Convertor | 方法 | infra 侧调用 |
|---|---|---|
| CountryConvertor + CountryMapper | Entity→DTO (7 个方法) | 无 |
| BarCodeConvertor + BarCodeMapper | Cmd→Entity (1 个方法) | 无 |
| QRCodeConvertor + QRCodeMapper | Cmd→Entity (1 个方法) | 无 |
| CaptchaCodeConvertor + CaptchaCodeMapper | Cmd→Entity ×2, Entity→DTO ×2, gRPC 转换, Entity→CachePO | `CaptchaCodeGatewayImpl` 使用 `toCaptchaCodeCacheablePO()` |

## Goals / Non-Goals

**Goals:**
- 消除 `genix-application` → `genix-infra` 的直接依赖
- Convertor/Mapper 命名和文件组织与 iam 完全一致

**Non-Goals:**
- 不修改 genix-domain、genix-client、genix-adapter 的依赖关系
- 不修改任何业务逻辑或 API 行为
- 不涉及其他架构问题（mumu-extension God Module、服务间运行时耦合等）

## Decisions

### 决策 1: 命名约定与 iam 对齐

采用 iam 的既定命名：application 用 `*AssemblerConvertor/*AssemblerMapper`，infra 用 `*PersistenceConvertor/*PersistenceMapper`。

**替代方案**: 保留简短命名 `*Convertor/*Mapper`。该方案已在 iam 重构中被明确放弃（`c1c3af9f` rename commit），不一致的命名会增加维护成本。

### 决策 2: Country/BarCode/QRCode 整体移动

这三个 Convertor 完全没有 infra 侧消费者，无需拆分。整体从 `genix-infra` 移至 `genix-application` 并改名。

### 决策 3: CaptchaCode 按方法拆分

`CaptchaCodeMapper` 的 `toCaptchaCodeCacheablePO()` 是唯一的 infra 侧方法，拆分为：

```
Application:  CaptchaCodeAssemblerMapper (5 方法: Cmd→Entity ×2, Entity→DTO ×2, DTO→gRPC)
Infra:        CaptchaCodePersistenceMapper (1 方法: Entity→CachePO)
```

**替代方案**: 不拆分，直接在 `CaptchaCodeGatewayImpl` 中内联 PO 赋值。这会绕过 Convertor 约束，不符合 iam 模式。

### 决策 4: 文件组织

遵循 iam 的目录结构：

```
genix-application/src/main/java/baby/mumu/genix/application/
  {country,barcode,qrcode,captcha}/convertor/
    {Country,BarCode,QRCode,CaptchaCode}AssemblerConvertor.java
    {Country,BarCode,QRCode,CaptchaCode}AssemblerMapper.java

genix-infra/src/main/java/baby/mumu/genix/infra/
  captcha/
    convertor/CaptchaCodePersistenceConvertor.java
    mapper/CaptchaCodePersistenceMapper.java
```

## Risks / Trade-offs

- **import 路径大量变更**: 13 个文件的 import 需要更新，但无逻辑变更，IDE 可自动处理，`./gradlew build` 验证
- **CaptchaCodeGatewayImpl 改动**: 从注入 `CaptchaCodeConvertor` 改为 `CaptchaCodePersistenceConvertor`，仅影响一个文件，行为不变
- **无 API 变更**: client、domain、adapter 模块完全不受影响
