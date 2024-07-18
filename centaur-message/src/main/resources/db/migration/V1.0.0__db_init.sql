-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
-- 消息状态类型枚举
CREATE TYPE message_status AS ENUM (
    'READ',
    'UNREAD',
    'ARCHIVED'
    );
-- 订阅文本消息
create table subscription_text_message
(
    id                bigint                   not null
        constraint subscription_text_message_pk_id
            primary key,
    sender_id         bigint                   not null default 0,
    receiver_id       bigint                   not null default 0,
    message           varchar(500)             not null default '',
    message_status    message_status           not null default 'UNREAD',
    creation_time     timestamp with time zone not null default '1970-01-01 00:00:00+00',
    founder           bigint                   not null default 0,
    modifier          bigint                   not null default 0,
    modification_time timestamp with time zone not null default '1970-01-01 00:00:00+00'
);
-- 广播文本消息
create table broadcast_text_message
(
    id                bigint                   not null
        constraint broadcast_text_message_pk_id
            primary key,
    sender_id         bigint                   not null default 0,
    receiver_ids      bigint[]                 not null default '{}',
    read_quantity     bigint                   not null default 0,
    unread_quantity   bigint                   not null default 0,
    message           varchar(500)             not null default '',
    message_status    message_status           not null default 'UNREAD',
    creation_time     timestamp with time zone not null default '1970-01-01 00:00:00+00',
    founder           bigint                   not null default 0,
    modifier          bigint                   not null default 0,
    modification_time timestamp with time zone not null default '1970-01-01 00:00:00+00'
);
-- 广播消息已读数量等于接收者数量自动更新消息状态为已读触发器函数
CREATE OR REPLACE FUNCTION update_broadcast_text_message_status_if_length_matches()
    RETURNS TRIGGER AS
$$
BEGIN
    IF array_length(NEW.receiver_ids, 1) IS NOT NULL AND
       NEW.read_quantity = array_length(NEW.receiver_ids, 1) THEN
        NEW.message_status = 'READ';
    ELSE
        NEW.message_status = 'UNREAD';
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
-- 广播消息已读数量等于接收者数量自动更新消息状态为已读触发器
CREATE TRIGGER check_broadcast_text_message_status
    BEFORE INSERT OR UPDATE
    ON broadcast_text_message
    FOR EACH ROW
EXECUTE FUNCTION update_broadcast_text_message_status_if_length_matches();
-- 广播消息已读数量增长自动更新未读数量触发器函数
CREATE OR REPLACE FUNCTION update_unread_quantity_when_read_quantity_increases()
    RETURNS TRIGGER AS
$$
BEGIN
    IF NEW.read_quantity IS DISTINCT FROM OLD.read_quantity THEN
        NEW.unread_quantity = OLD.unread_quantity - (NEW.read_quantity - OLD.read_quantity);
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
-- 广播消息已读数量增长自动更新未读数量触发器
CREATE TRIGGER trigger_update_broadcast_text_message_unread_quantity
    BEFORE UPDATE
    ON broadcast_text_message
    FOR EACH ROW
EXECUTE FUNCTION update_unread_quantity_when_read_quantity_increases();
