-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
-- noinspection SpellCheckingInspection @ extension/"pg_trgm"
-- 添加gin索引
CREATE EXTENSION IF NOT EXISTS pg_trgm;
CREATE INDEX authorities_code_gin_index ON authorities USING gin (code gin_trgm_ops);
CREATE INDEX authorities_name_gin_index ON authorities USING gin (name gin_trgm_ops);
CREATE INDEX roles_code_gin_index ON roles USING gin (code gin_trgm_ops);
CREATE INDEX roles_name_gin_index ON roles USING gin (name gin_trgm_ops);
