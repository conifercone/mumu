-- @formatter:off
-- 语言偏好枚举
CREATE TYPE language AS ENUM (
    'ZH',
    'EN'
    );
-- users新增列
-- 语言偏好
ALTER TABLE users
    ADD COLUMN language language;
comment on column users.language is '语言偏好';
