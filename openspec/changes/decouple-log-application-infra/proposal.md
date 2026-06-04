## Why

Log 模块的 application 层直接 import infra 层的 convertor（`OperationLogConvertor`、`SystemLogConvertor`）以及 Kafka PO 类（`OperationLogKafkaPO`、`SystemLogKafkaPO`）和配置类（`LogProperties`），共 15 处跨层引用，违反依赖反转原则。storage 模块已解耦，log 模块需要跟上。

## What Changes

- **OperationLogConvertor 拆分**：拆为 application 层的 `OperationLogAssemblerConvertor` + `OperationLogAssemblerMapper`（Cmd→Entity、Entity→DTO、gRPC 转换）和 infra 层的 `OperationLogKafkaPersistenceConvertor` + `OperationLogKafkaPersistenceMapper`（Entity↔KafkaPO）及 `OperationLogEsPersistenceConvertor` + `OperationLogEsPersistenceMapper`（Entity↔EsPO）
- **SystemLogConvertor 拆分**：同上对称拆分
- **移除 application 层对 infra convertor 的 import**：7 个 CmdExe + 2 个 ServiceImpl 改用各自对应的 AssemblerConvertor
- **Kafka Consumer 不再直接引用 Kafka PO**：`OperationLogKafkaConsumer` 和 `SystemLogKafkaConsumer` 改为通过 domain gateway 接口消费，PO 反序列化逻辑封装在 infra 层
- **toEntity(SubmitCmd) 中的 ID 生成和时间戳逻辑上移到 AssemblerConvertor**
- **LogProperties 不再被 application 层直接引用**：consumer 需要的配置通过 Spring Boot 配置属性注入，不 import infra 的 `LogProperties` 类
- **log-application/build.gradle.kts 移除 `log-infra` 依赖**
- 遵循 storage/genix 已确立的命名及包结构模式

## Capabilities

### New Capabilities
- `log-hexagonal-conformance`: log 模块各层之间的 convertor/mapper 职责边界清晰，application 层通过 domain gateway 接口与 infra 交互，不直接依赖 infra 层具体类；Kafka Consumer 通过 domain 接口消费消息，不直接引用 Kafka PO 类型

### Modified Capabilities
<!-- None -->

## Impact

- **模块**: `log-application`、`log-infra`（主要修改）；`log-domain`、`log-client`（新增 gateway 方法和/或配置接口）
- **转换类**:
  - 新增: `OperationLogAssemblerConvertor`、`OperationLogAssemblerMapper`（application 层）
  - 新增: `SystemLogAssemblerConvertor`、`SystemLogAssemblerMapper`（application 层）
  - 新增: `OperationLogKafkaPersistenceConvertor`、`OperationLogKafkaPersistenceMapper`（infra 层）
  - 新增: `OperationLogEsPersistenceConvertor`、`OperationLogEsPersistenceMapper`（infra 层）
  - 新增: `SystemLogKafkaPersistenceConvertor`、`SystemLogKafkaPersistenceMapper`（infra 层）
  - 新增: `SystemLogEsPersistenceConvertor`、`SystemLogEsPersistenceMapper`（infra 层）
  - 删除: `OperationLogConvertor`、`OperationLogMapper`、`SystemLogConvertor`、`SystemLogMapper`
- **Executors**: `OperationLogSubmitCmdExe`、`OperationLogSaveCmdExe`、`OperationLogFindAllCmdExe`、`OperationLogQryCmdExe`、`SystemLogSubmitCmdExe`、`SystemLogSaveCmdExe`、`SystemLogFindAllCmdExe`
- **Services**: `OperationLogServiceImpl`、`SystemLogServiceImpl`
- **Consumers**: `OperationLogKafkaConsumer`、`SystemLogKafkaConsumer`
- **Gateway Impl**: `OperationLogGatewayImpl`、`SystemLogGatewayImpl`
