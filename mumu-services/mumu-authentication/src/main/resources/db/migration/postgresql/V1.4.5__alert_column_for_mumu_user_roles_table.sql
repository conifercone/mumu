-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
ALTER TABLE mumu_user_roles RENAME COLUMN "user_id" TO "account_id";
-- 删除旧索引
DROP INDEX IF EXISTS user_roles_user_id;
-- 创建新索引
CREATE INDEX user_roles_account_id ON mumu_user_roles(account_id);
