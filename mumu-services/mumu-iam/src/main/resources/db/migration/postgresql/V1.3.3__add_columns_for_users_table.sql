-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
ALTER TABLE users
    ADD COLUMN bio varchar(500) NOT NULL DEFAULT '';
ALTER TABLE users
    ADD COLUMN nick_name varchar(100) NOT NULL DEFAULT '';
ALTER TABLE users_archived
    ADD COLUMN bio varchar(500) NOT NULL DEFAULT '';
ALTER TABLE users_archived
    ADD COLUMN nick_name varchar(100) NOT NULL DEFAULT '';
comment on column users.bio is '个性签名';
comment on column users.nick_name is '昵称';
comment on column users_archived.bio is '个性签名';
comment on column users_archived.nick_name is '昵称';
