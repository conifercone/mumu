-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
ALTER TABLE users
    ADD CONSTRAINT users_id_not_zero CHECK (users.id != 0);
ALTER TABLE users_archived
    ADD CONSTRAINT users_archived_id_not_zero CHECK (users_archived.id != 0);
