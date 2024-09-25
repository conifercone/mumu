-- @formatter:off
-- users新增列
-- 时区
ALTER TABLE users
    ADD COLUMN time_zone varchar(200);
comment on column users.time_zone is '时区';
