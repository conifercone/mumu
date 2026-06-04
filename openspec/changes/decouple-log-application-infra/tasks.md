## 1. OperationLog 域 Assembler

- [x] 1.1 在 `log-application/operation/convertor/` 创建 `OperationLogAssemblerMapper`：包含 `toEntity(SubmitCmd)`、`toEntity(SaveCmd)`、`toEntity(FindAllCmd)`、`toOperationLogFindAllDTO(OperationLog)`、`toOperationLogQryDTO(OperationLog)`、`toOperationLogSubmitCmd(GrpcCmd)`（从旧 Mapper 搬移）
- [x] 1.2 在 `log-application/operation/convertor/` 创建 `OperationLogAssemblerConvertor`：包装 Mapper 方法，`toEntity(SubmitCmd)` 中包含 ID 生成（TraceIdUtils / PrimaryKeyGrpcService）和时间戳逻辑
- [x] 1.3 在 `log-application/build.gradle.kts` 添加 `genix-client` 和 `commons-lang3` 依赖

## 2. OperationLog 域 Persistence

- [x] 2.1 在 `log-infra/operation/mapper/` 创建 `OperationLogKafkaPersistenceMapper`：仅包含 `toKafkaPO(OperationLog)`（从旧 Mapper 搬移）
- [x] 2.2 在 `log-infra/operation/convertor/` 创建 `OperationLogKafkaPersistenceConvertor`：包装 KafkaPersistenceMapper
- [x] 2.3 在 `log-infra/operation/mapper/` 创建 `OperationLogEsPersistenceMapper`：包含 `toEsPO(OperationLog)`、`toEntity(EsPO)`（从旧 Mapper 搬移）
- [x] 2.4 在 `log-infra/operation/convertor/` 创建 `OperationLogEsPersistenceConvertor`：包装 EsPersistenceMapper

## 3. OperationLog 域消费者改造

- [x] 3.1 将 `LogProperties` 中四个 `public static final` 常量迁移到 `log-client` 的新建常量类（如 `LogConstants`）：`OPERATION_LOG_KAFKA_TOPIC_NAME`、`SYSTEM_LOG_KAFKA_TOPIC_NAME`、`OPERATION_LOG_ES_INDEX_NAME`、`SYSTEM_LOG_ES_INDEX_NAME`
- [x] 3.3 在 `OperationLogGateway` 接口新增 `void saveFromKafkaMessage(String message)` 方法
- [x] 3.4 在 `OperationLogGatewayImpl` 实现 `saveFromKafkaMessage()`：jsonMapper 反序列化为 KafkaPO → KafkaPersistenceConvertor 转为 SaveCmd → 调用现有 save 逻辑

## 4. OperationLog 域 Executor/Service/GatewayImpl 更新

 - [x] 4.1 修改 `OperationLogSubmitCmdExe`：注入 `OperationLogAssemblerConvertor` 替换 `OperationLogConvertor`
 - [x] 4.2 修改 `OperationLogSaveCmdExe`：注入 `OperationLogAssemblerConvertor` 替换 `OperationLogConvertor`
 - [x] 4.3 修改 `OperationLogFindAllCmdExe`：注入 `OperationLogAssemblerConvertor` 替换 `OperationLogConvertor`
 - [x] 4.4 修改 `OperationLogQryCmdExe`：注入 `OperationLogAssemblerConvertor` 替换 `OperationLogConvertor`
 - [x] 4.5 修改 `OperationLogServiceImpl`：注入 `OperationLogAssemblerConvertor` 替换 `OperationLogConvertor`
 - [x] 4.6 修改 `OperationLogGatewayImpl`：注入 `OperationLogKafkaPersistenceConvertor` + `OperationLogEsPersistenceConvertor` 替换 `OperationLogConvertor`

## 5. SystemLog 域 Assembler

 - [x] 5.1 在 `log-application/system/convertor/` 创建 `SystemLogAssemblerMapper`：包含 `toEntity(SubmitCmd)`、`toEntity(SaveCmd)`、`toEntity(FindAllCmd)`、`toSystemLogFindAllDTO(SystemLog)`、`toSystemLogSubmitCmd(GrpcCmd)`（从旧 Mapper 搬移）
 - [x] 5.2 在 `log-application/system/convertor/` 创建 `SystemLogAssemblerConvertor`：包装 Mapper 方法，`toEntity(SubmitCmd)` 中包含 ID 生成和时间戳逻辑

## 6. SystemLog 域 Persistence

 - [x] 6.1 在 `log-infra/system/mapper/` 创建 `SystemLogKafkaPersistenceMapper`：仅包含 `toKafkaPO(SystemLog)`（从旧 Mapper 搬移）
 - [x] 6.2 在 `log-infra/system/convertor/` 创建 `SystemLogKafkaPersistenceConvertor`：包装 KafkaPersistenceMapper
 - [x] 6.3 在 `log-infra/system/mapper/` 创建 `SystemLogEsPersistenceMapper`：包含 `toEsPO(SystemLog)`、`toEntity(EsPO)`（从旧 Mapper 搬移）
 - [x] 6.4 在 `log-infra/system/convertor/` 创建 `SystemLogEsPersistenceConvertor`：包装 EsPersistenceMapper

## 7. SystemLog 域消费者改造

 - [x] 7.1 修改 `SystemLogKafkaConsumer`：注入 `SystemLogGateway` 替代 `SystemLogConvertor` 和 `JsonMapper`；`@KafkaListener` topic 改用 `log-client` 中的常量
 - [x] 7.2 在 `SystemLogGateway` 接口新增 `void saveFromKafkaMessage(String message)` 方法
 - [x] 7.3 在 `SystemLogGatewayImpl` 实现 `saveFromKafkaMessage()`：jsonMapper 反序列化为 KafkaPO → KafkaPersistenceConvertor 转为 SaveCmd → 调用现有 save 逻辑

## 8. SystemLog 域 Executor/Service/GatewayImpl 更新

 - [x] 8.1 修改 `SystemLogSubmitCmdExe`：注入 `SystemLogAssemblerConvertor` 替换 `SystemLogConvertor`
 - [x] 8.2 修改 `SystemLogSaveCmdExe`：注入 `SystemLogAssemblerConvertor` 替换 `SystemLogConvertor`
 - [x] 8.3 修改 `SystemLogFindAllCmdExe`：注入 `SystemLogAssemblerConvertor` 替换 `SystemLogConvertor`
 - [x] 8.4 修改 `SystemLogServiceImpl`：注入 `SystemLogAssemblerConvertor` 替换 `SystemLogConvertor`
 - [x] 8.5 修改 `SystemLogGatewayImpl`：注入 `SystemLogKafkaPersistenceConvertor` + `SystemLogEsPersistenceConvertor` 替换 `SystemLogConvertor`

## 9. 依赖清理

 - [x] 9.1 从 `log-application/build.gradle.kts` 移除 `log-infra` 依赖
 - [x] 9.2 删除旧的 `OperationLogConvertor.java`、`OperationLogMapper.java`、`SystemLogConvertor.java`、`SystemLogMapper.java`

## 10. 验证

 - [x] 10.1 运行 `./gradlew :mumu-log:compileJava` 确保所有模块编译通过
 - [x] 10.3 手动确认 `log-application` 不再包含任何 `import baby.mumu.log.infra.**` 语句
