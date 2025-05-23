-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
-- 重命名表
ALTER TABLE mumu_users RENAME TO mumu_accounts;
ALTER TABLE mumu_users_archived RENAME TO mumu_accounts_archived;

-- 重命名主键约束
ALTER TABLE mumu_accounts RENAME CONSTRAINT users_pk_id TO accounts_pk_id;
ALTER TABLE mumu_accounts_archived RENAME CONSTRAINT users_archived_pk_id TO accounts_archived_pk_id;

-- 重命名唯一约束
ALTER TABLE mumu_accounts RENAME CONSTRAINT users_unique_username TO accounts_unique_username;
ALTER TABLE mumu_accounts RENAME CONSTRAINT users_unique_email TO accounts_unique_email;

ALTER TABLE mumu_accounts_archived RENAME CONSTRAINT users_archived_unique_username TO accounts_archived_unique_username;
ALTER TABLE mumu_accounts_archived RENAME CONSTRAINT users_archived_unique_email TO accounts_archived_unique_email;

-- 重命名 CHECK 约束
ALTER TABLE mumu_accounts RENAME CONSTRAINT users_id_not_zero TO accounts_id_not_zero;
ALTER TABLE mumu_accounts_archived RENAME CONSTRAINT users_archived_id_not_zero TO accounts_archived_id_not_zero;

-- 重命名表
ALTER TABLE mumu_user_roles RENAME TO mumu_account_roles;

-- 重命名主键约束
ALTER TABLE mumu_account_roles RENAME CONSTRAINT user_roles_pkey TO account_roles_pkey;

-- 重命名索引
ALTER INDEX user_roles_account_id RENAME TO account_roles_account_id;
ALTER INDEX user_roles_role_id RENAME TO account_roles_role_id;
