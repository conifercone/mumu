-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
-- 已读的接收者id集合
ALTER TABLE broadcast_text_message
    ADD COLUMN read_receiver_ids bigint[] not null default '{}';
comment on column broadcast_text_message.read_receiver_ids is '已读的接收者id集合';
-- 未读的接收者id集合
ALTER TABLE broadcast_text_message
    ADD COLUMN unread_receiver_ids bigint[] not null default '{}';
comment on column broadcast_text_message.unread_receiver_ids is '未读的接收者id集合';
