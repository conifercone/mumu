-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
-- 文本广播信息表添加触发器函数
CREATE OR REPLACE FUNCTION move_to_broadcast_text_messages_archive()
    RETURNS TRIGGER AS $$
BEGIN
    -- 检查字段值是否等于 'ARCHIVED'
    IF NEW.message_status = 'ARCHIVED' THEN
        -- 将数据插入到归档表
        INSERT INTO broadcast_text_message_archived (id, sender_id, receiver_ids, read_quantity, unread_quantity, message, message_status, creation_time, founder, modifier, modification_time, read_receiver_ids, unread_receiver_ids)
        VALUES (NEW.id, NEW.sender_id, NEW.receiver_ids, NEW.read_quantity, NEW.unread_quantity, NEW.message, NEW.message_status, NEW.creation_time, NEW.founder, NEW.modifier, NEW.modification_time, NEW.read_receiver_ids, NEW.unread_receiver_ids);

        -- 从源表中删除该行
        DELETE FROM broadcast_text_message WHERE id = NEW.id;
        -- 防止源表中再插入相同数据
        RETURN NULL;
    END IF;
    -- 如果条件不满足，则返回新行（更新操作时）
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
-- 文本广播信息表添加触发器
CREATE TRIGGER trigger_move_to_broadcast_text_messages_archive
    AFTER INSERT OR UPDATE ON broadcast_text_message
    FOR EACH ROW
EXECUTE FUNCTION move_to_broadcast_text_messages_archive();
