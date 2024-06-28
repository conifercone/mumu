-- @formatter:off
-- 性别枚举
CREATE TYPE sex AS ENUM (
    'MALE',
    'FEMALE',
    'GREY',
    'SEXLESS'
    );
-- users新增列
-- 性别
ALTER TABLE users
    ADD COLUMN sex sex;
comment on column users.sex is '性别';
-- 头像地址
ALTER TABLE users
    ADD COLUMN avatar_url varchar(200);
comment on column users.avatar_url is '头像地址';
-- 手机号
ALTER TABLE users
    ADD COLUMN phone varchar(200);
comment on column users.phone is '手机号';

