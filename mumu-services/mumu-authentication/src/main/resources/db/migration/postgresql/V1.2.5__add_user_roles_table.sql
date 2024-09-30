-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
CREATE TABLE user_roles (
                                  user_id BIGINT NOT NULL,
                                  role_id BIGINT NOT NULL,
                                  creation_time     timestamp with time zone default '1970-01-01 00:00:00+00'::timestamp with time zone not null,
                                  founder           bigint                   default 0                                                  not null,
                                  modifier          bigint                   default 0                                                  not null,
                                  modification_time timestamp with time zone default '1970-01-01 00:00:00+00'::timestamp with time zone not null,
                                  PRIMARY KEY (user_id, role_id)
);
INSERT INTO user_roles (user_id, role_id, creation_time, founder, modifier, modification_time) VALUES (1,1,  now(), 0, 0, now()) ON CONFLICT (user_id,role_id) DO NOTHING;
INSERT INTO user_roles (user_id, role_id, creation_time, founder, modifier, modification_time) VALUES (2,2,  now(), 0, 0, now()) ON CONFLICT (user_id,role_id) DO NOTHING;
CREATE INDEX user_roles_user_id ON user_roles(user_id);
CREATE INDEX user_roles_role_id ON user_roles(role_id);
alter table users
    drop constraint IF EXISTS users_roles_id_fk;
alter table users drop column if exists role_id;
alter table users_archived drop column if exists role_id;
comment on table user_roles is '账户角色关系表';
comment on column user_roles.user_id is '账户ID';
comment on column user_roles.role_id is '角色ID';
