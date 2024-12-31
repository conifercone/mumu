-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
-- 修改时间和创建时间默认值修改为当前UTC时间
ALTER TABLE broadcast_text_message ALTER COLUMN creation_time SET DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE broadcast_text_message ALTER COLUMN modification_time SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE broadcast_text_message_archived ALTER COLUMN creation_time SET DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE broadcast_text_message_archived ALTER COLUMN modification_time SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE broadcast_text_message_receivers ALTER COLUMN creation_time SET DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE broadcast_text_message_receivers ALTER COLUMN modification_time SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE subscription_text_message ALTER COLUMN creation_time SET DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE subscription_text_message ALTER COLUMN modification_time SET DEFAULT CURRENT_TIMESTAMP;

ALTER TABLE subscription_text_message_archived ALTER COLUMN creation_time SET DEFAULT CURRENT_TIMESTAMP;
ALTER TABLE subscription_text_message_archived ALTER COLUMN modification_time SET DEFAULT CURRENT_TIMESTAMP;

