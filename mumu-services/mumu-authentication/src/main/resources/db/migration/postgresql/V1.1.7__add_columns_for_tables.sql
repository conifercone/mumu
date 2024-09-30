-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
-- 添加已归档属性
alter table authorities
    add archived boolean default false not null;
comment on column authorities.archived is '已归档';
alter table roles
    add archived boolean default false not null;
comment on column roles.archived is '已归档';
alter table users
    add archived boolean default false not null;
comment on column users.archived is '已归档';
