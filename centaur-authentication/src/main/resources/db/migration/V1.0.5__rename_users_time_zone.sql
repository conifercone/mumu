-- @formatter:off
-- users时区字段重命名
-- noinspection SqlResolve
alter table users
    rename column time_zone to timezone;
