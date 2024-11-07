-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
INSERT INTO authority_paths (id, ancestor_id, descendant_id, depth, creation_time, founder, modifier, modification_time) VALUES (1,1,1,  0, now(), 0, 0, now()) ON CONFLICT (id) DO NOTHING;
INSERT INTO authority_paths (id, ancestor_id, descendant_id, depth, creation_time, founder, modifier, modification_time) VALUES (2,2,2,  0, now(), 0, 0, now()) ON CONFLICT (id) DO NOTHING;
INSERT INTO authority_paths (id, ancestor_id, descendant_id, depth, creation_time, founder, modifier, modification_time) VALUES (3,3,3,  0, now(), 0, 0, now()) ON CONFLICT (id) DO NOTHING;
