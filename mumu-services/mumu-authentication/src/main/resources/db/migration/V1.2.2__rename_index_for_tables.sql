-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
-- 更改索引名称
ALTER INDEX authorities_pk_code RENAME TO authorities_unique_code;
ALTER INDEX authorities_archived_pk_code RENAME TO authorities_archived_unique_code;
ALTER INDEX roles_pk_code RENAME TO roles_unique_code;
ALTER INDEX roles_archived_pk_code RENAME TO roles_archived_unique_code;
ALTER INDEX user_addresses_pk_user_id RENAME TO user_addresses_unique_user_id;
ALTER INDEX users_pk_email RENAME TO users_unique_email;
ALTER INDEX users_pk_username RENAME TO users_unique_username;
ALTER INDEX users_archived_pk_username RENAME TO users_archived_unique_username;
ALTER INDEX users_archived_pk_email RENAME TO users_archived_unique_email;
