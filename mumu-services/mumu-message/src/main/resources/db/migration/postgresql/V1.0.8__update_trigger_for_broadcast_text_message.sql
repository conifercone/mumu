-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
-- 文本广播信息表更新触发器函数
CREATE OR REPLACE FUNCTION update_broadcast_text_message_status_if_length_matches()
    RETURNS TRIGGER AS
$$
BEGIN
    IF NEW.message_status <> 'ARCHIVED' THEN
        IF array_length(NEW.receiver_ids, 1) IS NOT NULL AND
           NEW.read_quantity = array_length(NEW.receiver_ids, 1) THEN
            NEW.message_status = 'READ';
        ELSE
            NEW.message_status = 'UNREAD';
        END IF;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

DROP TRIGGER IF EXISTS check_broadcast_text_message_status ON broadcast_text_message;
CREATE TRIGGER check_broadcast_text_message_status
    BEFORE INSERT OR UPDATE
    ON broadcast_text_message
    FOR EACH ROW
EXECUTE FUNCTION update_broadcast_text_message_status_if_length_matches();
