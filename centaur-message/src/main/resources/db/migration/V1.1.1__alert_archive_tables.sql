-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
alter table subscription_text_message_archived
    add archived boolean default true not null;
comment on column subscription_text_message.archived is '已归档';
alter table broadcast_text_message_archived
    add archived boolean default true not null;
comment on column broadcast_text_message.archived is '已归档';

CREATE OR REPLACE FUNCTION move_to_subscription_text_messages()
    RETURNS TRIGGER AS $$
BEGIN
    IF NEW.archived = false THEN
        INSERT INTO subscription_text_message (id, sender_id, receiver_id, message, message_status, creation_time, founder, modifier, modification_time)
        VALUES (NEW.id, NEW.sender_id, NEW.receiver_id, NEW.message, NEW.message_status, NEW.creation_time, NEW.founder, NEW.modifier, NEW.modification_time);

        DELETE FROM subscription_text_message_archived WHERE id = NEW.id;
        RETURN NULL;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER trigger_move_to_subscription_text_messages
    AFTER UPDATE ON subscription_text_message_archived
    FOR EACH ROW
EXECUTE FUNCTION move_to_subscription_text_messages();

CREATE OR REPLACE FUNCTION move_to_broadcast_text_messages()
    RETURNS TRIGGER AS $$
BEGIN
    IF NEW.archived = true THEN
        INSERT INTO broadcast_text_message (id, sender_id, receiver_ids, read_quantity, unread_quantity, message, message_status, creation_time, founder, modifier, modification_time, read_receiver_ids, unread_receiver_ids)
        VALUES (NEW.id, NEW.sender_id, NEW.receiver_ids, NEW.read_quantity, NEW.unread_quantity, NEW.message, NEW.message_status, NEW.creation_time, NEW.founder, NEW.modifier, NEW.modification_time, NEW.read_receiver_ids, NEW.unread_receiver_ids);

        DELETE FROM broadcast_text_message_archived WHERE id = NEW.id;
        RETURN NULL;
    END IF;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;
CREATE TRIGGER trigger_move_to_broadcast_text_messages
    AFTER UPDATE ON broadcast_text_message_archived
    FOR EACH ROW
EXECUTE FUNCTION move_to_broadcast_text_messages();
