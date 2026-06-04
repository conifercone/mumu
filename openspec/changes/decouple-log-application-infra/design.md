## Context

Log 模块有 hex 子模块架构，但 application 层 15 处 import infra 类：

- 7 个 CmdExe + 2 个 ServiceImpl 依赖 `OperationLogConvertor` / `SystemLogConvertor`
- 2 个 Kafka Consumer 直接依赖 `OperationLogKafkaPO` / `SystemLogKafkaPO`（Kafka 反序列化）
- Consumer 还直接引用 `LogProperties`（infra 配置类）

Infra 层 Mapper 混在一起：
- `OperationLogMapper` 9 个方法中 7 个处理 client 层类型（Cmd、DTO、gRPC）
- 同一个 Mapper 同时映射 KafkaPO 和 EsPO
- `OperationLogConvertor.toEntity(SubmitCmd)` 包含 gRPC ID 生成和时间戳逻辑

## Goals / Non-Goals

**Goals:**
- application 层不再 import infra 层任何类
- Cmd→Entity、Entity→DTO、gRPC 转换放 application 层
- Entity↔KafkaPO、Entity↔EsPO 转换放 infra 层
- `toEntity(SubmitCmd)` 中的 ID 生成和时间戳逻辑上移到 AssemblerConvertor
- Kafka Consumer 通过 domain gateway 接口消费，不直接引用 Kafka PO 类型
- Consumer 的 Kafka topic 名称通过 `log-client` 中的常量引用（常量从 `LogProperties` 迁移至 `log-client`），不引用 `LogProperties` 类
- 移除 `log-application/build.gradle.kts` 中的 `log-infra` 依赖

**Non-Goals:**
- 不改变 public API
- 不改变 domain 层实体结构
- 不新增测试

## Decisions

### Decision 1: 遵循已确立的 Assembler/Persistence 拆分模式

- Application 层：`*AssemblerMapper` + `*AssemblerConvertor`
- Infra 层：`*KafkaPersistenceMapper` + `*KafkaPersistenceConvertor`、`*EsPersistenceMapper` + `*EsPersistenceConvertor`

Kafka 和 ES 各有独立 PersistenceMapper，职责单一。

**Alternatives considered:**
- 合并 Kafka + ES 到一个 PersistenceMapper → 职责混杂，不采用

### Decision 2: Kafka Consumer 解耦方式

Consumer 当前流程：
```
Kafka消息(String) → jsonMapper → KafkaPO → Convertor → SaveCmd → Service.save()
```

重构后：
```
Kafka消息(String) → OperationLogGateway.handleKafkaMessage(String)
                   └→ (infra 层) jsonMapper → KafkaPO → KafkaPersistenceConvertor → SaveCmd → Service.save()
```

Consumer 只注入 `OperationLogGateway`（domain 接口），调用 `handleKafkaMessage(String)`。GatewayImpl 在 infra 层处理反序列化、PO 转换、委托 Service 保存。

但 `OperationLogGateway` 接口定义在 domain 层，`save` 方法的返回类型是 `void`。当前的 consumer 调 `operationLogService.save()` 实际上是 application 层的 service。重构后改为 consumer 直接调 gateway 或通过 service 间接调 gateway。

更简洁方案：Consumer 改为只持有 `OperationLogService`（application 层的接口，在 client 模块），service 新增 `saveFromKafkaMessage(String)` 方法，由 service 调用 gateway。这样 consumer 完全不碰 infra。

实际上，`OperationLogService` 在 `log-client` 模块，consumer 已经依赖它。只需在 service 接口新增方法，由 service impl 委托 gateway。这是最小改动方案。


`LogProperties` 作为 `@ConfigurationProperties` 绑定类留在 `log-infra`（绑定 ES/Kafka 基础设施配置是 infra 的职责）。但其四个 `public static final` 常量（`OPERATION_LOG_KAFKA_TOPIC_NAME`、`SYSTEM_LOG_KAFKA_TOPIC_NAME`、`OPERATION_LOG_ES_INDEX_NAME`、`SYSTEM_LOG_ES_INDEX_NAME`）迁移到 `log-client` 的 `LogClientConfiguration` 或新建常量类中。这些常量本质上是服务间通信的契约标识，属于 client 层的职责范围。Consumer 通过 `log-client` 引用这些常量，无需接触 infra。n
### Decision 4:

和 storage 的 `FileConvertor.toEntity()` 一样，`OperationLogSubmitCmdExe` 当前：
```java
operationLogConvertor.toEntity(cmd) → 生成 ID (traceId 或 snowflake) + 设置 operatingTime
```

重构后 `OperationLogAssemblerConvertor.toEntity(SubmitCmd)` 处理 ID 生成和时间戳，注入 `PrimaryKeyGrpcService` 和 `TraceIdUtils`（static 调用，不需要注入）。

### Decision 5: 包路径

```
log-application/.../application/operation/convertor/
  ├── OperationLogAssemblerConvertor.java
  └── OperationLogAssemblerMapper.java

log-infra/.../infra/operation/mapper/
  ├── OperationLogKafkaPersistenceMapper.java
  └── OperationLogEsPersistenceMapper.java

log-infra/.../infra/operation/convertor/
  ├── OperationLogKafkaPersistenceConvertor.java
  └── OperationLogEsPersistenceConvertor.java
```

SystemLog 同构对称。

## Risks / Trade-offs

- [Risk] Consumer 解耦引入 `OperationLogService.saveFromKafkaMessage(String)` 新方法，需要在 service 接口（client 模块）和实现（application 模块）都新增 → Mitigation: 改动小，纯委托
- [Risk] `@KafkaListener` 的 topic 从常量改为 SpEL 表达式，可能因配置缺失启动失败 → Mitigation: topic 名称在 LogProperties 中为硬编码常量，直接内联等价
- [Risk] `PrimaryKeyGrpcService` 从 infra 移到 application（OperationLog domain 需要 snowflake） → Mitigation: application 层已依赖 genix-client，无需额外配置
