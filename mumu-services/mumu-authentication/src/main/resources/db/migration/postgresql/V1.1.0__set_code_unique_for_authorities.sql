-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- code添加唯一约束
ALTER TABLE authorities
    ADD CONSTRAINT unique_code UNIQUE (code);
