-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
-- users时区字段重命名
alter table users
    rename column time_zone to timezone;
