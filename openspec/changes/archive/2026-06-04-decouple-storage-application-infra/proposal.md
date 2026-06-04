## Why

Storage 模块的 application 层直接 import infra 层的 convertor（`FileConvertor`、`StorageZoneConvertor`），infra 层的 mapper 同时处理 Cmd→Entity、Entity→DTO 等 application 层关注点，违反依赖反转原则。genix 模块已完成同类解耦，storage 需要跟上。

## What Changes

- **FileConvertor 拆分**：拆为 application 层的 `FileAssemblerConvertor` + `FileAssemblerMapper`（Cmd→Entity、Entity→DTO）和 infra 层的 `FilePersistenceConvertor` + `FilePersistenceMapper`（Entity↔PO）
- **StorageZoneConvertor 拆分**：拆为 application 层的 `StorageZoneAssemblerConvertor` + `StorageZoneAssemblerMapper`（Cmd→Entity）和 infra 层的 `StorageZonePersistenceConvertor` + `StorageZonePersistenceMapper`（Entity↔PO）
- **移除 application 层对 infra convertor 的 import**：`FileUploadCmdExe`、`FileFindMetaByMetaIdCmdExe`、`StorageZoneAddCmdExe` 改用各自对应的 AssemblerConvertor
- **FileConvertor.toEntity(storageZoneId, multipartFile) 逻辑上移到 application 层**：ID 生成、Zone 查询、MIME 检测等组装逻辑由 application 层协调，不再塞在 infra convertor 里
- **FileConvertor 移除对 StorageZoneRepository 和 StorageZoneConvertor 的跨域依赖**
- 遵循 genix 重构已确立的命名及包结构模式（`*.application.*.convertor.*Assembler*` / `*.infra.*.convertor.*Persistence*`）

## Capabilities

### New Capabilities
- `storage-hexagonal-conformance`: storage 模块各层之间的 convertor/mapper 职责边界清晰，application 层通过 domain gateway 接口与 infra 交互，不直接依赖 infra 层具体类

### Modified Capabilities
<!-- None -->

## Impact

- **模块**: `storage-application`、`storage-infra`（主要修改）；`storage-domain`、`storage-client`（无改动，但新增的 Assembler 引用 client 类型）
- **转换类**:
  - 新增: `FileAssemblerConvertor`、`FileAssemblerMapper`（application 层）
  - 新增: `StorageZoneAssemblerConvertor`、`StorageZoneAssemblerMapper`（application 层）
  - 拆分: `FileConvertor` → `FileAssemblerConvertor`（application）+ `FilePersistenceConvertor`（infra）
  - 拆分: `StorageZoneConvertor` → `StorageZoneAssemblerConvertor`（application）+ `StorageZonePersistenceConvertor`（infra）
  - 拆分: `FileMapper` → `FileAssemblerMapper`（application）+ `FilePersistenceMapper`（infra）
  - 拆分: `StorageZoneMapper` → `StorageZoneAssemblerMapper`（application）+ `StorageZonePersistenceMapper`（infra）
- **Executors**: `FileUploadCmdExe`、`FileFindMetaByMetaIdCmdExe`、`StorageZoneAddCmdExe`
- **Gateway Impl**: `FileGatewayImpl`、`StorageZoneGatewayImpl`（改引用 PersistenceConvertor）
