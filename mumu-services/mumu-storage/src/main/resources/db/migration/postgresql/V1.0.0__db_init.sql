-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
CREATE TABLE mumu_file_metadata (
                            id BIGINT NOT NULL constraint mumu_file_metadata_pk
                                primary key,
                            original_filename varchar(255) NOT NULL,
                            stored_filename   varchar(255) NOT NULL,
                            content_type      varchar(255) NOT NULL,
                            size BIGINT NOT NULL,
                            storage_zone varchar(255) NOT NULL,
                            storage_path varchar(255) NOT NULL,
                            creation_time     timestamp with time zone default CURRENT_TIMESTAMP not null,
                            founder           bigint                   default 0                 not null,
                            modifier          bigint                   default 0                 not null,
                            modification_time timestamp with time zone default CURRENT_TIMESTAMP not null
);
comment on table mumu_file_metadata is '文件元数据';
comment on column mumu_file_metadata.id is '唯一标识符';
comment on column mumu_file_metadata.original_filename is '原始文件名';
comment on column mumu_file_metadata.stored_filename is '存储的文件名';
comment on column mumu_file_metadata.content_type is '内容类型';
comment on column mumu_file_metadata.size is '大小';
comment on column mumu_file_metadata.storage_zone is '存储区域';
comment on column mumu_file_metadata.storage_path is '存储路径';
comment on column mumu_file_metadata.creation_time is '创建时间';
comment on column mumu_file_metadata.founder is '创建人';
comment on column mumu_file_metadata.modifier is '修改人';
comment on column mumu_file_metadata.modification_time is '修改时间';
