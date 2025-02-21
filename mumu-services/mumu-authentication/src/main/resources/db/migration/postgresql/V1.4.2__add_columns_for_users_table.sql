-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
ALTER TABLE mumu_users ADD COLUMN phone_verified bool NOT NULL DEFAULT false;
ALTER TABLE mumu_users ADD COLUMN email_verified bool NOT NULL DEFAULT false;
comment on column mumu_users.phone_verified is '手机号已验证';
comment on column mumu_users.email_verified is '邮箱已验证';
ALTER TABLE mumu_users_archived ADD COLUMN phone_verified bool NOT NULL DEFAULT false;
ALTER TABLE mumu_users_archived ADD COLUMN email_verified bool NOT NULL DEFAULT false;
comment on column mumu_users_archived.phone_verified is '手机号已验证';
comment on column mumu_users_archived.email_verified is '邮箱已验证';
