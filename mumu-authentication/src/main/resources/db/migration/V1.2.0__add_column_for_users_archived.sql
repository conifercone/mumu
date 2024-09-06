-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
-- 地址ID
ALTER TABLE users_archived
    ADD COLUMN address_id bigint default 0 not null;
comment on column users_archived.address_id is '用户地址id';
