-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
ALTER TABLE mumu_file_metadata ADD COLUMN storage_path varchar(255) NOT NULL DEFAULT '';
