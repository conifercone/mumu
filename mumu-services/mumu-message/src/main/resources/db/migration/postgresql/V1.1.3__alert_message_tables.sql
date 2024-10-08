-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
-- 删除message_status类型
ALTER TABLE broadcast_text_message ALTER COLUMN message_status type varchar(100) using message_status::varchar;
ALTER TABLE broadcast_text_message ALTER COLUMN message_status set default 'UNREAD';
ALTER TABLE broadcast_text_message_archived ALTER COLUMN message_status type varchar(100) using message_status::varchar;
ALTER TABLE broadcast_text_message_archived ALTER COLUMN message_status set default 'UNREAD';
ALTER TABLE subscription_text_message ALTER COLUMN message_status type varchar(100) using message_status::varchar;
ALTER TABLE subscription_text_message ALTER COLUMN message_status set default 'UNREAD';
ALTER TABLE subscription_text_message_archived ALTER COLUMN message_status type varchar(100) using message_status::varchar;
ALTER TABLE subscription_text_message_archived ALTER COLUMN message_status set default 'UNREAD';
drop type if exists message_status;
-- 按照数据库范式重构表结构
-- 新增文本广播消息接收者关系表
CREATE TABLE broadcast_text_message_receivers (
                            message_id BIGINT NOT NULL,
                            receiver_id BIGINT NOT NULL,
                            message_status varchar(100) default 'UNREAD' not null,
                            creation_time     timestamp with time zone default '1970-01-01 00:00:00+00'::timestamp with time zone not null,
                            founder           bigint                   default 0                                                  not null,
                            modifier          bigint                   default 0                                                  not null,
                            modification_time timestamp with time zone default '1970-01-01 00:00:00+00'::timestamp with time zone not null,
                            PRIMARY KEY (message_id, receiver_id)
);
CREATE INDEX broadcast_text_message_receivers_message_id ON broadcast_text_message_receivers(message_id);
CREATE INDEX broadcast_text_message_receivers_receiver_id ON broadcast_text_message_receivers(receiver_id);
CREATE INDEX broadcast_text_message_receivers_message_status_index on broadcast_text_message_receivers (message_status);
comment on table broadcast_text_message_receivers is '新增文本广播消息接收者关系表';
comment on column broadcast_text_message_receivers.message_id is '消息ID';
comment on column broadcast_text_message_receivers.receiver_id is '接收者ID';
comment on column broadcast_text_message_receivers.message_status is '消息状态';
-- 删除无用列 & 索引 & 触发器函数 & 触发器
-- 删除无用列
alter table broadcast_text_message drop column if exists receiver_ids;
alter table broadcast_text_message drop column if exists read_quantity;
alter table broadcast_text_message drop column if exists unread_quantity;
alter table broadcast_text_message drop column if exists read_receiver_ids;
alter table broadcast_text_message drop column if exists unread_receiver_ids;
alter table broadcast_text_message_archived drop column if exists receiver_ids;
alter table broadcast_text_message_archived drop column if exists read_quantity;
alter table broadcast_text_message_archived drop column if exists unread_quantity;
alter table broadcast_text_message_archived drop column if exists read_receiver_ids;
alter table broadcast_text_message_archived drop column if exists unread_receiver_ids;
-- 删除触发器函数并级联删除触发器
DROP FUNCTION IF EXISTS adjust_read_quantity_length() CASCADE;
DROP FUNCTION IF EXISTS adjust_unread_quantity_length() CASCADE;
DROP FUNCTION IF EXISTS update_broadcast_text_message_status_if_length_matches() CASCADE;
DROP FUNCTION IF EXISTS update_unread_quantity_when_read_quantity_increases() CASCADE;
