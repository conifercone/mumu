-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
-- 添加索引
CREATE INDEX broadcast_text_message_message_status_index ON broadcast_text_message (message_status);
CREATE INDEX broadcast_text_message_sender_id_index ON broadcast_text_message (sender_id);
CREATE INDEX subscription_text_message_message_status_index ON subscription_text_message (message_status);
CREATE INDEX subscription_text_message_sender_id_index ON subscription_text_message (sender_id);
CREATE INDEX subscription_text_message_receiver_id_index ON subscription_text_message (receiver_id);
