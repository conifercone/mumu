-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
CREATE INDEX idx_authority_paths_combination ON authority_paths (ancestor_id, descendant_id, depth);
