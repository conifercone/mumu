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
    id                bigint      not null
        constraint subscription_text_message_pk_id
            primary key,
    subscription_account_id     bigint not null default 0,
    from_account_id   bigint not null default 0,
    message           varchar(500) not null default '',
    message_status    message_status not null default 'UNREAD',
    creation_time     timestamp with time zone not null default '1970-01-01 00:00:00+00',
    founder           bigint not null default 0,
    modifier          bigint not null default 0,
    modification_time timestamp with time zone not null default '1970-01-01 00:00:00+00'
);
-- 广播文本消息
create table broadcast_text_message
(
    id                bigint      not null
        constraint broadcast_text_message_pk_id
            primary key,
    from_account_id   bigint not null default 0,
    message           varchar(500) not null default '',
    message_status    message_status not null default 'UNREAD',
    creation_time     timestamp with time zone not null default '1970-01-01 00:00:00+00',
    founder           bigint not null default 0,
    modifier          bigint not null default 0,
    modification_time timestamp with time zone not null default '1970-01-01 00:00:00+00'
);
