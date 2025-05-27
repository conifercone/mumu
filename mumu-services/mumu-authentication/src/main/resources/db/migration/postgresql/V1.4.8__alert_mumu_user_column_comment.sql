-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
COMMENT ON TABLE mumu_accounts IS '账号表';
COMMENT ON COLUMN mumu_accounts.username IS '账号名';
COMMENT ON COLUMN mumu_accounts.id IS '账号ID';
COMMENT ON COLUMN mumu_accounts.account_non_locked IS '账号未锁定';

COMMENT ON TABLE mumu_accounts_archived IS '账号归档表';
COMMENT ON COLUMN mumu_accounts_archived.username IS '账号名';
COMMENT ON COLUMN mumu_accounts_archived.id IS '账号ID';
COMMENT ON COLUMN mumu_accounts_archived.account_non_locked IS '账号未锁定';

COMMENT ON TABLE mumu_account_roles IS '账号角色关系表';
COMMENT ON COLUMN mumu_account_roles.account_id IS '账号ID';
