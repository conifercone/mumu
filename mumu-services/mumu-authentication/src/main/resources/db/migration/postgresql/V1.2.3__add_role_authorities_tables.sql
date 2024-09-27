-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
CREATE TABLE role_authorities (
                              role_id BIGINT NOT NULL,
                              authority_id BIGINT NOT NULL,
                              creation_time     timestamp with time zone default '1970-01-01 00:00:00+00'::timestamp with time zone not null,
                              founder           bigint                   default 0                                                  not null,
                              modifier          bigint                   default 0                                                  not null,
                              modification_time timestamp with time zone default '1970-01-01 00:00:00+00'::timestamp with time zone not null,
                              PRIMARY KEY (role_id, authority_id)
);
INSERT INTO role_authorities (role_id, authority_id, creation_time, founder, modifier, modification_time) VALUES (1,1,  now(), 0, 0, now()) ON CONFLICT (role_id,authority_id) DO NOTHING;
INSERT INTO role_authorities (role_id, authority_id, creation_time, founder, modifier, modification_time) VALUES (1,2,  now(), 0, 0, now()) ON CONFLICT (role_id,authority_id) DO NOTHING;
CREATE INDEX role_authorities_role_id ON role_authorities(role_id);
CREATE INDEX role_authorities_authority_id ON role_authorities(authority_id);
alter table roles drop column if exists authorities;
alter table roles_archived drop column if exists authorities;
comment on table role_authorities is '角色权限关系表';
comment on column role_authorities.role_id is '角色ID';
comment on column role_authorities.authority_id is '权限ID';
alter table user_addresses drop column if exists archived;
