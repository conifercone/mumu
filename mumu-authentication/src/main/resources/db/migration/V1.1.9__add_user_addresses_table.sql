-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
-- 用户地址表
CREATE TABLE user_addresses (
                                id                bigint                                                                              not null
                                    constraint user_addresses_pk_id
                                        primary key,
                                user_id           bigint                   default 0                                                  not null,
                                street            varchar(255)             default ''::character varying                              not null,
                                city              varchar(100)             default ''::character varying                              not null,
                                state             varchar(100)             default ''::character varying                              not null,
                                postal_code       varchar(20)              default ''::character varying                              not null,
                                country           varchar(100)             default ''::character varying                              not null,
                                creation_time     timestamp with time zone default '1970-01-01 00:00:00+00'::timestamp with time zone not null,
                                founder           bigint                   default 0                                                  not null,
                                modifier          bigint                   default 0                                                  not null,
                                modification_time timestamp with time zone default '1970-01-01 00:00:00+00'::timestamp with time zone not null,
                                archived          boolean                  default false                                              not null
);

comment on column user_addresses.id is '唯一主键';
comment on column user_addresses.user_id is '账户ID';
comment on column user_addresses.street is '街道地址，包含门牌号和街道信息';
comment on column user_addresses.city is '城市信息';
comment on column user_addresses.state is '州或省的信息';
comment on column user_addresses.postal_code is '邮政编码';
comment on column user_addresses.country is '国家信息';
comment on column user_addresses.creation_time is '创建时间';
comment on column user_addresses.founder is '创建人';
comment on column user_addresses.modifier is '修改人';
comment on column user_addresses.modification_time is '修改时间';
comment on column user_addresses.archived is '已归档';
comment on table user_addresses is '账户地址表';

-- 地址ID
ALTER TABLE users
    ADD COLUMN address_id bigint default 0 not null;
comment on column users.address_id is '用户地址id';
