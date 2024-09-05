-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
-- noinspection SpellCheckingInspection @ extension/"pg_trgm"
-- message添加gin索引
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX broadcast_text_message_message_gin_index ON broadcast_text_message USING gin (message gin_trgm_ops);
CREATE INDEX subscription_text_message_message_gin_index ON subscription_text_message USING gin (message gin_trgm_ops);
