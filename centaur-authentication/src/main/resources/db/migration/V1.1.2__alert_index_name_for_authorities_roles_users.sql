-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
-- 更改索引名称
ALTER INDEX unique_code RENAME TO authorities_pk_code;
ALTER INDEX authorities_pk RENAME TO authorities_pk_id;
ALTER INDEX roles_pk RENAME TO roles_pk_id;
ALTER INDEX users_pk RENAME TO users_pk_id;
