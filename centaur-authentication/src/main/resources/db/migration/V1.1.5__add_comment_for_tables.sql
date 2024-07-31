-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
-- 添加注释
comment on table authorities is '权限表';
comment on column roles.id is '角色ID';
comment on column roles.code is '角色编码';
comment on column roles.authorities is '角色所包含的权限ID集合';
comment on column roles.creation_time is '创建时间';
comment on column roles.founder is '创建人';
comment on column roles.modifier is '修改人';
comment on column roles.modification_time is '修改时间';
comment on table users is '账户表';
comment on column users.id is '账户ID';
comment on column users.username is '账户名';
comment on column users.password is '密码';
comment on column users.enabled is '已启用';
comment on column users.credentials_non_expired is '凭证未过期';
comment on column users.account_non_locked is '帐户未锁定';
comment on column users.account_non_expired is '帐号未过期';
comment on column users.role_id is '角色ID';
comment on column users.creation_time is '创建时间';
comment on column users.founder is '创建人';
comment on column users.modifier is '修改人';
comment on column users.modification_time is '修改时间';
