-- @formatter:off
-- noinspection SqlSignatureForFile @ routine/"move_archived_records"
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
-- 添加归档表及触发器
create table authorities_archived
(
    code              varchar(50)                                                                         not null
        constraint authorities_archived_pk_code
            unique,
    id                bigint                                                                              not null
        constraint authorities_archived_pk_id
            primary key,
    name              varchar(200)             default ''::character varying                              not null,
    creation_time     timestamp with time zone default '1970-01-01 00:00:00+00'::timestamp with time zone not null,
    founder           bigint                   default 0                                                  not null,
    modifier          bigint                   default 0                                                  not null,
    modification_time timestamp with time zone default '1970-01-01 00:00:00+00'::timestamp with time zone not null,
    archived          boolean                  default true                                              not null
);

comment on table authorities_archived is '权限归档表';

comment on column authorities_archived.code is '权限编码';

comment on column authorities_archived.id is '唯一主键';

comment on column authorities_archived.name is '权限名称';

comment on column authorities_archived.creation_time is '创建时间';

comment on column authorities_archived.founder is '创建人';

comment on column authorities_archived.modifier is '修改人';

comment on column authorities_archived.modification_time is '修改时间';

comment on column authorities_archived.archived is '已归档';

alter table authorities_archived
    owner to root;

create index authorities_archived_code_gin_index
    on authorities_archived using gin (code public.gin_trgm_ops);

create index authorities_archived_name_gin_index
    on authorities_archived using gin (name public.gin_trgm_ops);

create table roles_archived
(
    id                bigint                                                                              not null
        constraint roles_archived_pk_id
            primary key,
    name              varchar(200)             default ''::character varying                              not null,
    code              varchar(100)                                                                        not null
        constraint roles_archived_pk_code
            unique,
    authorities       bigint[]                 default '{}'::bigint[]                                     not null,
    creation_time     timestamp with time zone default '1970-01-01 00:00:00+00'::timestamp with time zone not null,
    founder           bigint                   default 0                                                  not null,
    modifier          bigint                   default 0                                                  not null,
    modification_time timestamp with time zone default '1970-01-01 00:00:00+00'::timestamp with time zone not null,
    archived          boolean                  default true                                              not null
);

comment on table roles_archived is '角色归档表';

comment on column roles_archived.id is '角色ID';

comment on column roles_archived.name is '角色名';

comment on column roles_archived.code is '角色编码';

comment on column roles_archived.authorities is '角色所包含的权限ID集合';

comment on column roles_archived.creation_time is '创建时间';

comment on column roles_archived.founder is '创建人';

comment on column roles_archived.modifier is '修改人';

comment on column roles_archived.modification_time is '修改时间';

comment on column roles_archived.archived is '已归档';

alter table roles_archived
    owner to root;

create index roles_archived_code_gin_index
    on roles_archived using gin (code public.gin_trgm_ops);

create index roles_archived_name_gin_index
    on roles_archived using gin (name public.gin_trgm_ops);

create table users_archived
(
    username                varchar(50)                                                                         not null
        constraint users_archived_pk_username
            unique,
    password                varchar(500)                                                                        not null,
    enabled                 boolean                                                                             not null,
    id                      bigint                                                                              not null
        constraint users_archived_pk_id
            primary key,
    credentials_non_expired boolean                  default false                                              not null,
    account_non_locked      boolean                  default false                                              not null,
    account_non_expired     boolean                  default false                                              not null,
    role_id                 bigint                   default 0                                                  not null,
    creation_time           timestamp with time zone default '1970-01-01 00:00:00+00'::timestamp with time zone not null,
    founder                 bigint                   default 0                                                  not null,
    modifier                bigint                   default 0                                                  not null,
    modification_time       timestamp with time zone default '1970-01-01 00:00:00+00'::timestamp with time zone not null,
    sex                     sex                      default 'SEXLESS'::sex                                     not null,
    avatar_url              varchar(200)             default ''::character varying                              not null,
    phone                   varchar(200)             default ''::character varying                              not null,
    email                   varchar(200)             default ''::character varying                              not null
        constraint users_archived_pk_email
            unique,
    timezone                varchar(200)             default ''::character varying                              not null,
    language                language                 default 'ZH'::language                                     not null,
    birthday                date                     default '1970-01-01'::date                                 not null,
    archived                boolean                  default true                                              not null
);

comment on table users_archived is '账户归档表';

comment on column users_archived.username is '账户名';

comment on column users_archived.password is '密码';

comment on column users_archived.enabled is '已启用';

comment on column users_archived.id is '账户ID';

comment on column users_archived.credentials_non_expired is '凭证未过期';

comment on column users_archived.account_non_locked is '帐户未锁定';

comment on column users_archived.account_non_expired is '帐号未过期';

comment on column users_archived.role_id is '角色ID';

comment on column users_archived.creation_time is '创建时间';

comment on column users_archived.founder is '创建人';

comment on column users_archived.modifier is '修改人';

comment on column users_archived.modification_time is '修改时间';

comment on column users_archived.sex is '性别';

comment on column users_archived.avatar_url is '头像地址';

comment on column users_archived.phone is '手机号';

comment on column users_archived.email is '电子邮箱';

comment on column users_archived.timezone is '时区';

comment on column users_archived.language is '语言偏好';

comment on column users_archived.birthday is '生日';

comment on column users_archived.archived is '已归档';

alter table users_archived
    owner to root;
