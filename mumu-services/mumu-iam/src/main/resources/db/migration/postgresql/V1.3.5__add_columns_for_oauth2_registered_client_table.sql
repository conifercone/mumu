-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
ALTER TABLE oauth2_registered_client
    ADD COLUMN creation_time timestamp with time zone NOT NULL DEFAULT '1970-01-01 00:00:00+00'::timestamp with time zone;
ALTER TABLE oauth2_registered_client
    ADD COLUMN founder bigint NOT NULL DEFAULT 0;
ALTER TABLE oauth2_registered_client
    ADD COLUMN modification_time timestamp with time zone NOT NULL DEFAULT '1970-01-01 00:00:00+00'::timestamp with time zone;
ALTER TABLE oauth2_registered_client
    ADD COLUMN modifier bigint NOT NULL DEFAULT 0;
-- client_id增加唯一索引
ALTER TABLE oauth2_registered_client
    ADD CONSTRAINT oauth2_registered_client_pk_client_id UNIQUE (client_id);
comment on column oauth2_registered_client.creation_time is '创建时间';
comment on column oauth2_registered_client.founder is '创建人';
comment on column oauth2_registered_client.modification_time is '修改时间';
comment on column oauth2_registered_client.modifier is '修改人';
