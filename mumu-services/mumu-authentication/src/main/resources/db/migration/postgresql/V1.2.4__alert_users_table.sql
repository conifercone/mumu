-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
alter table users drop column if exists address_id;
alter table users_archived drop column if exists address_id;
alter table user_addresses
    drop constraint IF EXISTS user_addresses_unique_user_id;
CREATE INDEX user_addresses_user_id_index ON user_addresses (user_id);
