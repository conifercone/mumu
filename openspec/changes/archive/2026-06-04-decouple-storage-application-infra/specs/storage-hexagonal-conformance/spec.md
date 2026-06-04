## ADDED Requirements

### Requirement: Application layer SHALL NOT depend on infra convertor classes
Application 层的 executor 类 SHALL NOT import 或注入 infra 层的 convertor/mapper。Cmd→Entity 和 Entity→DTO 转换 SHALL 通过 application 层自身的 AssemblerConvertor 完成。

#### Scenario: FileUploadCmdExe uses FileAssemblerConvertor
- **WHEN** FileUploadCmdExe 需要将 MultipartFile 转换为 File 实体
- **THEN** 它调用 FileAssemblerConvertor.toEntity(storageZoneId, multipartFile)，该 convertor 位于 storage-application 模块

#### Scenario: FileFindMetaByMetaIdCmdExe uses FileAssemblerConvertor
- **WHEN** FileFindMetaByMetaIdCmdExe 需要将 FileMetadata 转换为 FileFindMetaByMetaIdDTO
- **THEN** 它调用 FileAssemblerConvertor.toFileFindMetaByMetaIdDTO(fileMetadata)，该 convertor 位于 storage-application 模块

#### Scenario: StorageZoneAddCmdExe uses StorageZoneAssemblerConvertor
- **WHEN** StorageZoneAddCmdExe 需要将 StorageZoneAddCmd 转换为 StorageZone 实体
- **THEN** 它调用 StorageZoneAssemblerConvertor.toEntity(storageZoneAddCmd)，该 convertor 位于 storage-application 模块

### Requirement: Infra mapper SHALL only handle Entity↔PO transformations
Infra 层的 MapStruct mapper SHALL 仅定义 Entity↔PO 之间的映射方法，SHALL NOT 引用 client 层的 Cmd、DTO、gRPC 类型。

#### Scenario: FilePersistenceMapper only maps FileMetadata to and from PO
- **WHEN** infra 层需要持久化映射
- **THEN** FilePersistenceMapper 仅包含 toFileMetadataPO(FileMetadata) 和 toEntity(FileMetadataPO) 方法

#### Scenario: StorageZonePersistenceMapper only maps StorageZone to and from PO
- **WHEN** infra 层需要持久化映射
- **THEN** StorageZonePersistenceMapper 仅包含 toStorageZonePO(StorageZone) 和 toEntity(StorageZonePO) 方法

### Requirement: File domain assembly logic SHALL reside in application layer
FileConvertor.toEntity 中的 ID 生成、StorageZone 查询、MIME 类型检测等组装逻辑 SHALL 从 infra 层迁移到 application 层的 FileAssemblerConvertor。

#### Scenario: FileAssemblerConvertor orchestrates entity creation
- **WHEN** 上传文件时需要创建 File 实体
- **THEN** FileAssemblerConvertor.toEntity 调用 PrimaryKeyGrpcService.snowflake() 生成 ID，通过 StorageZoneGateway.findById() 查询 StorageZone，使用 Tika 检测 MIME 类型，组装并返回 File 实体

### Requirement: FileConvertor SHALL NOT depend on StorageZone domain classes
FileConvertor 重构后的 FilePersistenceConvertor SHALL NOT 注入 StorageZoneRepository 或 StorageZoneConvertor。跨域查询 SHALL 通过 StorageZoneGateway domain 接口协调。

#### Scenario: No cross-domain dependency in infra
- **WHEN** infra 层的 File 相关类需要获取 StorageZone 信息
- **THEN** 该查询在 application 层通过 StorageZoneGateway.findById() 完成，infra 层不持有对 StorageZone 仓库的直接引用
