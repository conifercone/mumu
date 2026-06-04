## 1. StorageZone 域解耦

- [x] 1.1 在 `storage-application/zone/convertor/` 创建 `StorageZoneAssemblerMapper`（MapStruct 接口）：仅包含 `toEntity(StorageZoneAddCmd)`（从旧 `StorageZoneMapper` 搬移）
- [x] 1.2 在 `storage-application/zone/convertor/` 创建 `StorageZoneAssemblerConvertor`（手写 wrapper）：包装 `StorageZoneAssemblerMapper.INSTANCE.toEntity()`
- [x] 1.3 在 `storage-infra/zone/mapper/` 创建 `StorageZonePersistenceMapper`（MapStruct 接口）：仅包含 `toStorageZonePO(StorageZone)`、`toEntity(StorageZonePO)`（从旧 `StorageZoneMapper` 搬移）
- [x] 1.4 在 `storage-infra/zone/convertor/` 创建 `StorageZonePersistenceConvertor`（手写 wrapper）：包装 `StorageZonePersistenceMapper.INSTANCE` 的两个方法
- [x] 1.5 修改 `StorageZoneAddCmdExe`：注入 `StorageZoneAssemblerConvertor` 替换 `StorageZoneConvertor`
- [x] 1.6 修改 `StorageZoneGatewayImpl`：注入 `StorageZonePersistenceConvertor` 替换 `StorageZoneConvertor`
- [x] 1.7 删除旧的 `storage-infra/zone/convertor/StorageZoneConvertor.java` 和 `StorageZoneMapper.java`

## 2. File 域解耦

- [x] 2.1 在 `storage-application/file/convertor/` 创建 `FileAssemblerMapper`（MapStruct 接口）：仅包含 `toFileFindMetaByMetaIdDTO(FileMetadata)`（从旧 `FileMapper` 搬移）
- [x] 2.2 在 `storage-application/file/convertor/` 创建 `FileAssemblerConvertor`（手写 wrapper）：包含 `toEntity(storageZoneId, multipartFile)`（从旧 `FileConvertor` 搬移并重构）和 `toFileFindMetaByMetaIdDTO(FileMetadata)`
- [x] 2.3 `FileAssemblerConvertor.toEntity()` 改为通过 `StorageZoneGateway.findById()` 查询 Zone，而非直接调用 `StorageZoneRepository`
- [x] 2.4 在 `storage-application/build.gradle.kts` 添加 `tika-core` 依赖
- [x] 2.5 在 `storage-infra/file/mapper/` 创建 `FilePersistenceMapper`（MapStruct 接口）：仅包含 `toFileMetadataPO(FileMetadata)`、`toEntity(FileMetadataPO)`（从旧 `FileMapper` 搬移）
- [x] 2.6 在 `storage-infra/file/convertor/` 创建 `FilePersistenceConvertor`（手写 wrapper）：包装 `FilePersistenceMapper.INSTANCE` 的两个方法，保留 `FileMetadataPO` set `storageZoneId` 的逻辑
- [x] 2.7 修改 `FileUploadCmdExe`：注入 `FileAssemblerConvertor` 替换 `FileConvertor`
- [x] 2.8 修改 `FileFindMetaByMetaIdCmdExe`：注入 `FileAssemblerConvertor` 替换 `FileConvertor`
- [x] 2.9 修改 `FileGatewayImpl`：注入 `FilePersistenceConvertor` 替换 `FileConvertor`
- [x] 2.10 删除旧的 `storage-infra/file/convertor/FileConvertor.java` 和 `FileMapper.java`

## 3. StorageZoneGateway 接口扩展

- [x] 3.1 在 `StorageZoneGateway` 接口新增 `Optional<StorageZone> findById(Long id)` 方法
- [x] 3.2 在 `StorageZoneGatewayImpl` 实现 `findById()`：通过 `StorageZoneRepository.findById()` 查询，使用 `StorageZonePersistenceConvertor` 转换 PO→Entity

## 4. 验证

- [x] 4.1 运行 `./gradlew :mumu-storage:compileJava` 确保所有模块编译通过
- [x] 4.2 运行 `./gradlew :mumu-storage:test` 确保无回归
- [x] 4.3 手动确认 `storage-application` 不再包含任何 `import baby.mumu.storage.infra.**` 语句
