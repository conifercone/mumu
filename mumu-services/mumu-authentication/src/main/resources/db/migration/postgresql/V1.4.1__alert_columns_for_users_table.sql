-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
ALTER TABLE mumu_users ALTER COLUMN enabled SET DEFAULT true;
ALTER TABLE mumu_users ALTER COLUMN credentials_non_expired SET DEFAULT true;
ALTER TABLE mumu_users ALTER COLUMN account_non_expired SET DEFAULT true;
ALTER TABLE mumu_users ALTER COLUMN account_non_locked SET DEFAULT true;
ALTER TABLE mumu_users_archived ALTER COLUMN enabled SET DEFAULT true;
ALTER TABLE mumu_users_archived ALTER COLUMN credentials_non_expired SET DEFAULT true;
ALTER TABLE mumu_users_archived ALTER COLUMN account_non_expired SET DEFAULT true;
ALTER TABLE mumu_users_archived ALTER COLUMN account_non_locked SET DEFAULT true;
