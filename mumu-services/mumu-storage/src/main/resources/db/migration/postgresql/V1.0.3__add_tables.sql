-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
CREATE TABLE mumu_file_storage_zones
(
    id                BIGINT                                             NOT NULL
        constraint mumu_file_storage_zones_pk
            primary key,
    code              varchar(255)                                       NOT NULL,
    name              varchar(255)                                       NOT NULL default '',
    description       varchar(500)                                       NOT NULL default '',
    policy            varchar(100)                                       NOT NULL default 'PRIVATE',
    creation_time     timestamp with time zone default CURRENT_TIMESTAMP not null,
    founder           bigint                   default 0                 not null,
    modifier          bigint                   default 0                 not null,
    modification_time timestamp with time zone default CURRENT_TIMESTAMP not null
);
insert into mumu_file_storage_zones(id, code, name, description, policy, creation_time, founder,
                                    modifier, modification_time)
VALUES (0, 'public', '公共存储', '公共存储区域', 'PUBLIC', CURRENT_TIMESTAMP, 0, 0,
        CURRENT_TIMESTAMP);
insert into mumu_file_storage_zones(id, code, name, description, policy, creation_time, founder,
                                    modifier, modification_time)
VALUES (1, 'avatars', '头像存储区域', '头像存储区域', 'PUBLIC', CURRENT_TIMESTAMP, 0, 0,
        CURRENT_TIMESTAMP);
comment on table mumu_file_storage_zones is '文件存储区域基本信息';
comment on column mumu_file_storage_zones.id is '唯一标识符';
comment on column mumu_file_storage_zones.code is '编码';
comment on column mumu_file_storage_zones.name is '名称';
comment on column mumu_file_storage_zones.description is '描述信息';
comment on column mumu_file_storage_zones.policy is '策略';
comment on column mumu_file_storage_zones.creation_time is '创建时间';
comment on column mumu_file_storage_zones.founder is '创建人';
comment on column mumu_file_storage_zones.modifier is '修改人';
comment on column mumu_file_storage_zones.modification_time is '修改时间';
COMMENT ON TABLE mumu_file_metadata IS '文件元数据基本信息';
ALTER TABLE mumu_file_metadata
    RENAME COLUMN storage_zone TO storage_zone_id;
ALTER TABLE mumu_file_metadata
    ALTER COLUMN storage_zone_id TYPE BIGINT USING storage_zone_id::BIGINT;
