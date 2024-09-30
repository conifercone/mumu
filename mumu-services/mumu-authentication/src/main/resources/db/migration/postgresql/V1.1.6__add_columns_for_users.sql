-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
-- 生日
ALTER TABLE users
    ADD COLUMN birthday Date not null default '1970-01-01';
comment on column users.birthday is '生日';
