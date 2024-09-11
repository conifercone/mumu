-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
-- noinspection SpellCheckingInspection @ extension/"pg_trgm"
-- 文本广播信息表添加触发器
-- 自动调整已读数量触发器函数
CREATE OR REPLACE FUNCTION adjust_read_quantity_length()
    RETURNS TRIGGER AS $$
BEGIN
    -- 如果read_quantity大于receiver_ids的长度，则将read_quantity设置为receiver_ids的长度
    IF NEW.read_quantity > array_length(NEW.receiver_ids, 1) THEN
        NEW.read_quantity := array_length(NEW.receiver_ids, 1);
    END IF;

    -- 如果read_quantity小于0，则将read_quantity设置为0
    IF NEW.read_quantity < 0 THEN
        NEW.read_quantity := 0;
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
-- 自动调整已读数量触发器
CREATE TRIGGER adjust_read_quantity_trigger
    BEFORE INSERT OR UPDATE ON broadcast_text_message
    FOR EACH ROW EXECUTE FUNCTION adjust_read_quantity_length();

-- 自动调整未读数量触发器函数
CREATE OR REPLACE FUNCTION adjust_unread_quantity_length()
    RETURNS TRIGGER AS $$
BEGIN
    -- 如果unread_quantity大于receiver_ids的长度，则将unread_quantity设置为receiver_ids的长度
    IF NEW.unread_quantity > array_length(NEW.receiver_ids, 1) THEN
        NEW.unread_quantity := array_length(NEW.receiver_ids, 1);
    END IF;
    -- 如果unread_quantity小于0，则将unread_quantity设置为0
    IF NEW.unread_quantity < 0 THEN
        NEW.unread_quantity := 0;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
-- 自动调整未读数量触发器
CREATE TRIGGER adjust_unread_quantity_trigger
    BEFORE INSERT OR UPDATE ON broadcast_text_message
    FOR EACH ROW EXECUTE FUNCTION adjust_unread_quantity_length();
