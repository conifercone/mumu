-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
-- 创建一个新的枚举类型
ALTER TYPE message_status RENAME TO message_status_old;

CREATE TYPE message_status AS ENUM (
    'READ',
    'UNREAD'
    );
alter table subscription_text_message
    add archived boolean default false not null;
comment on column subscription_text_message.archived is '已归档';
alter table broadcast_text_message
    add archived boolean default false not null;
comment on column broadcast_text_message.archived is '已归档';

-- 更新表中列的类型
ALTER TABLE subscription_text_message ALTER COLUMN message_status DROP DEFAULT;
ALTER TABLE subscription_text_message_archived ALTER COLUMN message_status DROP DEFAULT;
ALTER TABLE broadcast_text_message ALTER COLUMN message_status DROP DEFAULT;
ALTER TABLE broadcast_text_message_archived ALTER COLUMN message_status DROP DEFAULT;
ALTER TABLE subscription_text_message
    ALTER COLUMN message_status TYPE message_status
        USING message_status::text::message_status;
ALTER TABLE subscription_text_message_archived
    ALTER COLUMN message_status TYPE message_status
        USING message_status::text::message_status;
ALTER TABLE broadcast_text_message
    ALTER COLUMN message_status TYPE message_status
        USING message_status::text::message_status;
ALTER TABLE broadcast_text_message_archived
    ALTER COLUMN message_status TYPE message_status
        USING message_status::text::message_status;
ALTER TABLE subscription_text_message ALTER COLUMN message_status SET DEFAULT 'UNREAD'::message_status;
ALTER TABLE subscription_text_message_archived ALTER COLUMN message_status SET DEFAULT 'UNREAD'::message_status;
ALTER TABLE broadcast_text_message ALTER COLUMN message_status SET DEFAULT 'UNREAD'::message_status;
ALTER TABLE broadcast_text_message_archived ALTER COLUMN message_status SET DEFAULT 'UNREAD'::message_status;
-- 删除旧的枚举类型
DROP TYPE message_status_old;

-- 修改现有触发器函数
CREATE OR REPLACE FUNCTION update_broadcast_text_message_status_if_length_matches()
    RETURNS TRIGGER AS
$$
BEGIN
    IF array_length(NEW.receiver_ids, 1) IS NOT NULL AND
       NEW.read_quantity = array_length(NEW.receiver_ids, 1) THEN
        NEW.message_status = 'READ';
    ELSE
        NEW.message_status = 'UNREAD';
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


CREATE OR REPLACE FUNCTION move_to_subscription_text_messages_archive()
    RETURNS TRIGGER AS $$
BEGIN
    IF NEW.archived = true THEN
        INSERT INTO subscription_text_message_archived (id, sender_id, receiver_id, message, message_status, creation_time, founder, modifier, modification_time)
        VALUES (NEW.id, NEW.sender_id, NEW.receiver_id, NEW.message, NEW.message_status, NEW.creation_time, NEW.founder, NEW.modifier, NEW.modification_time);

        DELETE FROM subscription_text_message WHERE id = NEW.id;
        RETURN NULL;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
DROP TRIGGER IF EXISTS trigger_move_to_subscription_text_messages_archive ON subscription_text_message;
CREATE TRIGGER trigger_move_to_subscription_text_messages_archive
    AFTER INSERT OR UPDATE ON subscription_text_message
    FOR EACH ROW
EXECUTE FUNCTION move_to_subscription_text_messages_archive();

CREATE OR REPLACE FUNCTION move_to_broadcast_text_messages_archive()
    RETURNS TRIGGER AS $$
BEGIN
    IF NEW.archived = true THEN
        INSERT INTO broadcast_text_message_archived (id, sender_id, receiver_ids, read_quantity, unread_quantity, message, message_status, creation_time, founder, modifier, modification_time, read_receiver_ids, unread_receiver_ids)
        VALUES (NEW.id, NEW.sender_id, NEW.receiver_ids, NEW.read_quantity, NEW.unread_quantity, NEW.message, NEW.message_status, NEW.creation_time, NEW.founder, NEW.modifier, NEW.modification_time, NEW.read_receiver_ids, NEW.unread_receiver_ids);

        DELETE FROM broadcast_text_message WHERE id = NEW.id;
        RETURN NULL;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
DROP TRIGGER IF EXISTS trigger_move_to_broadcast_text_messages_archive ON broadcast_text_message;
CREATE TRIGGER trigger_move_to_broadcast_text_messages_archive
    AFTER INSERT OR UPDATE ON broadcast_text_message
    FOR EACH ROW
EXECUTE FUNCTION move_to_broadcast_text_messages_archive();
