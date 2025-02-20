-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
ALTER TABLE mumu_users RENAME COLUMN sex TO gender;
ALTER TABLE mumu_users_archived RENAME COLUMN sex TO gender;
ALTER TABLE mumu_users ALTER COLUMN gender SET DEFAULT 'PREFER_NOT_TO_SAY';
ALTER TABLE mumu_users_archived ALTER COLUMN gender SET DEFAULT 'PREFER_NOT_TO_SAY';
