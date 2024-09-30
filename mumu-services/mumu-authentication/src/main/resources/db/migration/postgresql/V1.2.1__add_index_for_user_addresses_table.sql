-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
ALTER TABLE user_addresses
    ADD CONSTRAINT user_addresses_pk_user_id UNIQUE (user_id);
