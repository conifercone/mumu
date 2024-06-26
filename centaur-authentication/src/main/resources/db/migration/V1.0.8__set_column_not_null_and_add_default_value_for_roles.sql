-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- roles name字段设置为NOT NULL,添加空字符串为默认值
insert into roles(id, name, code, authorities, creation_time, founder, modifier, modification_time)
VALUES (0, '游客', 'tourists', '{}', '2024-03-08 06:47:49.970747+00:00',0 , 0,
        '2024-03-08 06:47:49.970747+00:00');
ALTER TABLE roles
    ALTER COLUMN name SET DEFAULT '';
UPDATE roles
SET name = ''
WHERE name IS NULL;
ALTER TABLE roles
    ALTER COLUMN name SET NOT NULL;
-- roles authorities字段设置为NOT NULL,添加{}为默认值
ALTER TABLE roles
    ALTER COLUMN authorities SET DEFAULT '{}';
UPDATE roles
SET authorities = '{}'
WHERE authorities IS NULL;
ALTER TABLE roles
    ALTER COLUMN authorities SET NOT NULL;
-- roles creation_time字段设置为NOT NULL,添加unix时间戳零点为默认值
ALTER TABLE roles
    ALTER COLUMN creation_time SET DEFAULT '1970-01-01 00:00:00+00';
UPDATE roles
SET creation_time = '1970-01-01 00:00:00+00'
WHERE creation_time IS NULL;
ALTER TABLE roles
    ALTER COLUMN creation_time SET NOT NULL;
-- roles modification_time字段设置为NOT NULL,添加unix时间戳零点为默认值
ALTER TABLE roles
    ALTER COLUMN modification_time SET DEFAULT '1970-01-01 00:00:00+00';
UPDATE roles
SET modification_time = '1970-01-01 00:00:00+00'
WHERE modification_time IS NULL;
ALTER TABLE roles
    ALTER COLUMN modification_time SET NOT NULL;
-- roles founder字段设置为NOT NULL,添加0为默认值
ALTER TABLE roles
    ALTER COLUMN founder SET DEFAULT 0;
UPDATE roles
SET founder = 0
WHERE founder IS NULL;
ALTER TABLE roles
    ALTER COLUMN founder SET NOT NULL;
-- roles modifier字段设置为NOT NULL,添加0为默认值
ALTER TABLE roles
    ALTER COLUMN modifier SET DEFAULT 0;
UPDATE roles
SET modifier = 0
WHERE modifier IS NULL;
ALTER TABLE roles
    ALTER COLUMN modifier SET NOT NULL;
