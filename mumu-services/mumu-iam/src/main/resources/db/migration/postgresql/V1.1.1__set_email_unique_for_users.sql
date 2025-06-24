-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
-- email添加唯一约束
ALTER TABLE users
    ADD CONSTRAINT users_pk_email UNIQUE (email);
