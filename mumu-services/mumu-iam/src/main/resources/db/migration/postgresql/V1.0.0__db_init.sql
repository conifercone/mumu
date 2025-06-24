-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
create table authorities
(
    code              varchar(50) not null,
    id                bigint      not null
        constraint authorities_pk
            primary key,
    name              varchar(200),
    creation_time     timestamp with time zone,
    founder           bigint,
    modifier          bigint,
    modification_time timestamp with time zone
);

alter table authorities
    owner to root;

create table oauth2_registered_client
(
    id                            varchar(100)                            not null
        primary key,
    client_id                     varchar(100)                            not null,
    client_id_issued_at           timestamp     default CURRENT_TIMESTAMP not null,
    client_secret                 varchar(200)  default NULL::character varying,
    client_secret_expires_at      timestamp,
    client_name                   varchar(200)                            not null,
    client_authentication_methods varchar(1000)                           not null,
    authorization_grant_types     varchar(1000)                           not null,
    redirect_uris                 varchar(1000) default NULL::character varying,
    post_logout_redirect_uris     varchar(1000) default NULL::character varying,
    scopes                        varchar(1000)                           not null,
    client_settings               varchar(2000)                           not null,
    token_settings                varchar(2000)                           not null
);

alter table oauth2_registered_client
    owner to root;

create table oauth2_authorization_consent
(
    registered_client_id varchar(100)  not null,
    principal_name       varchar(200)  not null,
    authorities          varchar(1000) not null,
    primary key (registered_client_id, principal_name)
);

alter table oauth2_authorization_consent
    owner to root;

create table oauth2_authorization
(
    id                            varchar(100) not null
        primary key,
    registered_client_id          varchar(100) not null,
    principal_name                varchar(200) not null,
    authorization_grant_type      varchar(100) not null,
    authorized_scopes             varchar(1000) default NULL::character varying,
    attributes                    text,
    state                         varchar(500)  default NULL::character varying,
    authorization_code_value      text,
    authorization_code_issued_at  timestamp,
    authorization_code_expires_at timestamp,
    authorization_code_metadata   text,
    access_token_value            text,
    access_token_issued_at        timestamp,
    access_token_expires_at       timestamp,
    access_token_metadata         text,
    access_token_type             varchar(100)  default NULL::character varying,
    access_token_scopes           varchar(1000) default NULL::character varying,
    oidc_id_token_value           text,
    oidc_id_token_issued_at       timestamp,
    oidc_id_token_expires_at      timestamp,
    oidc_id_token_metadata        text,
    refresh_token_value           text,
    refresh_token_issued_at       timestamp,
    refresh_token_expires_at      timestamp,
    refresh_token_metadata        text,
    user_code_value               text,
    user_code_issued_at           timestamp,
    user_code_expires_at          timestamp,
    user_code_metadata            text,
    device_code_value             text,
    device_code_issued_at         timestamp,
    device_code_expires_at        timestamp,
    device_code_metadata          text
);

alter table oauth2_authorization
    owner to root;

create table roles
(
    id                bigint       not null
        constraint roles_pk
            primary key,
    name              varchar(200),
    code              varchar(100) not null
        constraint roles_pk_code
            unique,
    authorities       bigint[],
    creation_time     timestamp with time zone,
    founder           bigint,
    modifier          bigint,
    modification_time timestamp with time zone
);

comment on table roles is '角色表';

comment on column roles.name is '角色名';

alter table roles
    owner to root;

create table users
(
    username                varchar(50)  not null
        constraint users_pk_username
            unique,
    password                varchar(500) not null,
    enabled                 boolean      not null,
    id                      bigint       not null
        constraint users_pk
            primary key,
    credentials_non_expired boolean,
    account_non_locked      boolean,
    account_non_expired     boolean,
    role_id                 bigint
        constraint users_roles_id_fk
            references roles,
    creation_time           timestamp with time zone,
    founder                 bigint,
    modifier                bigint,
    modification_time       timestamp with time zone
);

alter table users
    owner to root;

