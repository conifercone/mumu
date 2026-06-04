## Context

Storage 模块已有 hex 子模块架构（`storage-client` / `storage-adapter` / `storage-application` / `storage-domain` / `storage-infra`），但 convertor/mapper 的职责划分违反依赖反转：

- application 层的 3 个 CmdExe 直接 import infra 层的 `FileConvertor` / `StorageZoneConvertor`
- infra 层的 `StorageZoneMapper` 处理 `StorageZoneAddCmd → StorageZone`（Cmd 是 client 类型）
- infra 层的 `FileMapper` 处理 `FileMetadata → FileFindMetaByMetaIdDTO`（DTO 是 client 类型）
- `FileConvertor.toEntity(storageZoneId, multipartFile)` 在 infra 层里做 ID 生成（gRPC）、Zone 查询（repository）、MIME 检测（Tika）
- `FileConvertor` 存在跨域依赖：注入 `StorageZoneRepository` 和 `StorageZoneConvertor`

genix 模块在 `0c221221` 已完成同类解耦，形成了可复用的模式。

## Goals / Non-Goals

**Goals:**
- application 层不再 import infra 层的任何 convertor/mapper 类
- Cmd→Entity、Entity→DTO、gRPC 转换放 application 层
- Entity↔PO 转换放 infra 层
- `FileConvertor.toEntity()` 里的组装逻辑上移到 application 层
- 移除 `FileConvertor` 对 `StorageZoneRepository` 和 `StorageZoneConvertor` 的跨域依赖
- 遵循 genix 已确立的命名和包结构模式

**Non-Goals:**
- 不改变 public API（REST 接口、gRPC 接口行为不变）
- 不改变 domain 层实体或 gateway 接口
- 不新增测试（本次纯重构，测试应在独立 change 中补充）

## Decisions

### Decision 1: 遵循 genix 已确立的拆分模式

genix 重构后将 convertor/mapper 拆为两组：

- Application 层：`*AssemblerMapper`（MapStruct 接口）+ `*AssemblerConvertor`（手写 wrapper）
- Infra 层：`*PersistenceMapper`（MapStruct 接口）+ `*PersistenceConvertor`（手写 wrapper）

storage 照此模式，避免引入新的命名或结构变体。

**Alternatives considered:**
- 在 domain 层定义 convertor 接口 → 过度设计，当前需求范围不需要
- 把 AssemblerMapper 和 PersistenceMapper 合并在一个 MapStruct 接口里，放在不同包 → 职责混在一起，不清晰

### Decision 2: `FileConvertor.toEntity(storageZoneId, multipartFile)` 的处理方式

该方法当前在 infra 层做：
1. 调用 `PrimaryKeyGrpcService.snowflake()` 生成 FileMetadata.id
2. 调用 `StorageZoneRepository.findById()` + `StorageZoneConvertor.toEntity()` 查 Zone
3. 从 `MultipartFile` 提取字节、文件名、大小
4. 用 Tika 检测 MIME 类型
5. 组装 File + FileMetadata

重构方案：
- 步骤 1-4 全部提升到 `FileAssemblerConvertor`（application 层），作为 `toEntity(storageZoneId, multipartFile)` 方法
- Zone 查询改为通过 `StorageZoneGateway.findById()` domain 接口调用，而非直接依赖 repository
- `FileAssemblerConvertor` 注入 `PrimaryKeyGrpcService`（client 调用）和 `StorageZoneGateway`（domain 接口）
- `FilePersistenceConvertor` 只保留 `toFileMetadataPO()` 和 `toEntity(FileMetadataPO)` 纯粹映射
- 安装 `org.apache.tika:tika-core` 到 `storage-application` 的 `build.gradle.kts` 依赖（当前仅在 `storage-infra` 有）

### Decision 3: File 域的包路径

```
storage-application/src/main/java/baby/mumu/storage/application/file/convertor/
  ├── FileAssemblerConvertor.java   (新增)
  └── FileAssemblerMapper.java      (新增)

storage-infra/src/main/java/baby/mumu/storage/infra/file/mapper/
  └── FilePersistenceMapper.java    (从 infra/.../convertor/FileMapper 拆分重命名)

storage-infra/src/main/java/baby/mumu/storage/infra/file/convertor/
  └── FilePersistenceConvertor.java (从 infra/.../convertor/FileConvertor 拆分重命名)

storage-domain/src/main/java/baby/mumu/storage/domain/zone/gateway/
  └── StorageZoneGateway.java       (可能需要新增 findById 方法)
```

注意：genix 的 PersistenceMapper 放在 `infra.*.mapper` 包，PersistenceConvertor 放在 `infra.*.convertor` 包。storage 保持一致。

### Decision 4: StorageZone 域的包路径

```
storage-application/src/main/java/baby/mumu/storage/application/zone/convertor/
  ├── StorageZoneAssemblerConvertor.java   (新增)
  └── StorageZoneAssemblerMapper.java      (新增)

storage-infra/src/main/java/baby/mumu/storage/infra/zone/mapper/
  └── StorageZonePersistenceMapper.java    (从 infra/.../convertor/StorageZoneMapper 拆分重命名)

storage-infra/src/main/java/baby/mumu/storage/infra/zone/convertor/
  └── StorageZonePersistenceConvertor.java (从 infra/.../convertor/StorageZoneConvertor 拆分重命名)
```

### Decision 5: `StorageZoneGateway` 扩展

`FileConvertor` 当前通过 `StorageZoneRepository` 直接查数据库。解耦后，`FileAssemblerConvertor` 应通过 domain gateway 接口查询。如果 `StorageZoneGateway` 尚不存在 `findById(Long)` 方法，需在 domain 接口新增，并在 `StorageZoneGatewayImpl` 中实现。

## Risks / Trade-offs

- [Risk] `PrimaryKeyGrpcService` 从 infra 层移到 application 层，虽然仍是跨模块 gRPC 调用，但 application 层调用 client 服务是合理的依赖方向 → Mitigation: 这是正确的架构方向，无实质风险
- [Risk] `StorageZoneGateway` 新增方法可能被 File 域以外的调用者误用 → Mitigation: `findById` 是标准 CRUD，语义明确，无滥用风险
- [Risk] Tika 依赖从 infra 移到 application，需要更新 `build.gradle.kts` → Mitigation: 常规依赖变更，`gradle/libs.versions.toml` 已有 tika 版本定义
