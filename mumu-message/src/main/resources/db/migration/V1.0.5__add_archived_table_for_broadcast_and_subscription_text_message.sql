-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
-- 文本订阅信息归档表
create table subscription_text_message_archived
(
    id                bigint                                                                              not null
        constraint subscription_text_message_archived_pk_id
            primary key,
    sender_id         bigint                   default 0 not null,
    receiver_id bigint default 0 not null,
    message varchar(500) default ''::character varying                                                    not null,
    message_status    message_status           default 'UNREAD'::message_status                           not null,
    creation_time     timestamp with time zone default '1970-01-01 00:00:00+00'::timestamp with time zone not null,
    founder           bigint                   default 0                                                  not null,
    modifier          bigint                   default 0                                                  not null,
    modification_time timestamp with time zone default '1970-01-01 00:00:00+00'::timestamp with time zone not null
);

alter table subscription_text_message_archived
    owner to root;

create index subscription_text_message_archived_message_status_index
    on subscription_text_message_archived (message_status);

create index subscription_text_message_archived_sender_id_index
    on subscription_text_message_archived (sender_id);

create index subscription_text_message_archived_receiver_id_index
    on subscription_text_message_archived (receiver_id);

create index subscription_text_message_archived_message_gin_index
    on subscription_text_message_archived using gin (message gin_trgm_ops);

-- 文本广播消息归档表
create table broadcast_text_message_archived
(
    id                  bigint                                                                              not null
        constraint broadcast_text_message_archived_pk_id
            primary key,
    sender_id           bigint                   default 0 not null,
    receiver_ids bigint[] default '{}'::bigint[] not null,
    read_quantity bigint default 0 not null,
    unread_quantity bigint default 0 not null,
    message varchar(500) default ''::character varying                                                      not null,
    message_status      message_status           default 'UNREAD'::message_status                           not null,
    creation_time       timestamp with time zone default '1970-01-01 00:00:00+00'::timestamp with time zone not null,
    founder             bigint                   default 0                                                  not null,
    modifier            bigint                   default 0                                                  not null,
    modification_time   timestamp with time zone default '1970-01-01 00:00:00+00'::timestamp with time zone not null,
    read_receiver_ids   bigint[]                 default '{}'::bigint[]                                     not null,
    unread_receiver_ids bigint[]                 default '{}'::bigint[]                                     not null
);

comment on column broadcast_text_message_archived.read_receiver_ids is '已读的接收者id集合';

comment on column broadcast_text_message_archived.unread_receiver_ids is '未读的接收者id集合';

alter table broadcast_text_message_archived
    owner to root;

create index broadcast_text_message_archived_message_status_index
    on broadcast_text_message_archived (message_status);

create index broadcast_text_message_archived_sender_id_index
    on broadcast_text_message_archived (sender_id);

create index broadcast_text_message_archived_message_gin_index
    on broadcast_text_message_archived using gin (message gin_trgm_ops);


