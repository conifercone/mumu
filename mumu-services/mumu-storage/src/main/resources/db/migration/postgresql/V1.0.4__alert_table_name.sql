-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
COMMENT ON TABLE mumu_file_storage_zones IS '存储区域基本信息';
ALTER TABLE mumu_file_storage_zones RENAME TO mumu_storage_zones;
ALTER INDEX mumu_file_storage_zones_pk RENAME TO mumu_storage_zones_pk;
