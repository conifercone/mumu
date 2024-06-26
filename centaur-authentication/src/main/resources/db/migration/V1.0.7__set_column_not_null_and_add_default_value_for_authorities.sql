-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- authorities name字段设置为NOT NULL,添加空字符串为默认值
ALTER TABLE authorities
    ALTER COLUMN name SET DEFAULT '';
UPDATE authorities
SET name = ''
WHERE name IS NULL;
ALTER TABLE authorities
    ALTER COLUMN name SET NOT NULL;
-- authorities creation_time字段设置为NOT NULL,添加unix时间戳零点为默认值
ALTER TABLE authorities
    ALTER COLUMN creation_time SET DEFAULT '1970-01-01 00:00:00+00';
UPDATE authorities
SET creation_time = '1970-01-01 00:00:00+00'
WHERE creation_time IS NULL;
ALTER TABLE authorities
    ALTER COLUMN creation_time SET NOT NULL;
-- authorities modification_time字段设置为NOT NULL,添加unix时间戳零点为默认值
ALTER TABLE authorities
    ALTER COLUMN modification_time SET DEFAULT '1970-01-01 00:00:00+00';
UPDATE authorities
SET modification_time = '1970-01-01 00:00:00+00'
WHERE modification_time IS NULL;
ALTER TABLE authorities
    ALTER COLUMN modification_time SET NOT NULL;
-- authorities founder字段设置为NOT NULL,添加0为默认值
ALTER TABLE authorities
    ALTER COLUMN founder SET DEFAULT 0;
UPDATE authorities
SET founder = 0
WHERE founder IS NULL;
ALTER TABLE authorities
    ALTER COLUMN founder SET NOT NULL;
-- authorities modifier字段设置为NOT NULL,添加0为默认值
ALTER TABLE authorities
    ALTER COLUMN modifier SET DEFAULT 0;
UPDATE authorities
SET modifier = 0
WHERE modifier IS NULL;
ALTER TABLE authorities
    ALTER COLUMN modifier SET NOT NULL;
