## ADDED Requirements

### Requirement: Application layer SHALL NOT depend on infra convertor classes
Application 层的 executor、service、consumer 类 SHALL NOT import 或注入 infra 层的 convertor/mapper。Cmd→Entity 和 Entity→DTO 转换 SHALL 通过 application 层自身的 AssemblerConvertor 完成。

#### Scenario: OperationLogSubmitCmdExe uses OperationLogAssemblerConvertor
- **WHEN** OperationLogSubmitCmdExe 需要将 SubmitCmd 转换为 OperationLog 实体
- **THEN** 它调用 OperationLogAssemblerConvertor.toEntity(submitCmd)，该 convertor 位于 log-application 模块

#### Scenario: OperationLogFindAllCmdExe uses OperationLogAssemblerConvertor
- **WHEN** OperationLogFindAllCmdExe 需要将 FindAllCmd 转换为 OperationLog 或将 OperationLog 转换为 DTO
- **THEN** 它调用 OperationLogAssemblerConvertor 的对应方法，该 convertor 位于 log-application 模块

### Requirement: Infra mapper SHALL only handle Entity↔PO transformations
Infra 层的 MapStruct mapper SHALL 仅定义 Entity↔PO 之间的映射方法，SHALL NOT 引用 client 层的 Cmd、DTO、gRPC 类型。Kafka 和 ES 各有独立的 PersistenceMapper。

#### Scenario: OperationLogKafkaPersistenceMapper only maps OperationLog to and from KafkaPO
- **WHEN** infra 层需要 Kafka 持久化映射
- **THEN** OperationLogKafkaPersistenceMapper 仅包含 toKafkaPO(OperationLog) 方法

#### Scenario: OperationLogEsPersistenceMapper only maps OperationLog to and from EsPO
- **WHEN** infra 层需要 ES 持久化映射
- **THEN** OperationLogEsPersistenceMapper 仅包含 toEsPO(OperationLog) 和 toEntity(EsPO) 方法

### Requirement: Kafka Consumer SHALL NOT reference Kafka PO types
OperationLogKafkaConsumer 和 SystemLogKafkaConsumer SHALL NOT 直接 import 或反序列化 Kafka PO 类型。Kafka 消息反序列化 SHALL 委托给 domain gateway 在 infra 层处理。

#### Scenario: Consumer delegates deserialization to gateway
- **WHEN** Kafka Consumer 收到消息字符串
- **THEN** 它调用 OperationLogGateway.handleKafkaMessage(String)，由 GatewayImpl 在 infra 层完成反序列化和保存

### Requirement: Consumer SHALL NOT reference LogProperties
Kafka Consumer 的 topic 名称 SHALL 通过 `log-client` 中的常量引用（常量已从 `LogProperties` 迁移至 `log-client`），SHALL NOT 通过 `LogProperties` 类引用。

#### Scenario: Consumer uses client-layer constant for topic name
- **WHEN** Kafka Consumer 声明 @KafkaListener
- **THEN** topics 参数使用 log-client 层的常量（如 LogConstants.OPERATION_LOG_KAFKA_TOPIC_NAME）而非 LogProperties.OPERATION_LOG_KAFKA_TOPIC_NAME

### Requirement: SubmitCmd entity creation SHALL happen in application layer
OperationLogConvertor.toEntity(SubmitCmd) 中的 ID 生成和时间戳设置 SHALL 在 application 层的 AssemblerConvertor 中完成。

#### Scenario: AssemblerConvertor generates ID and timestamp
- **WHEN** 需要从 SubmitCmd 创建 OperationLog 实体
- **THEN** OperationLogAssemblerConvertor.toEntity 调用 TraceIdUtils.getTraceId() 或 PrimaryKeyGrpcService.snowflake() 生成 ID，设置 operatingTime 为 UTC now
