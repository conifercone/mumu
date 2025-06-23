-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
-- users新增列
-- 电子邮箱
ALTER TABLE users
    ADD COLUMN email varchar(200);
comment on column users.email is '电子邮箱';

