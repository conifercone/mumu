-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- users credentials_non_expired字段设置为NOT NULL,添加false为默认值
ALTER TABLE users
    ALTER COLUMN credentials_non_expired SET DEFAULT false;
UPDATE users
SET credentials_non_expired = false
WHERE credentials_non_expired IS NULL;
ALTER TABLE users
    ALTER COLUMN credentials_non_expired SET NOT NULL;
-- users account_non_locked字段设置为NOT NULL,添加false为默认值
ALTER TABLE users
    ALTER COLUMN account_non_locked SET DEFAULT false;
UPDATE users
SET account_non_locked = false
WHERE account_non_locked IS NULL;
ALTER TABLE users
    ALTER COLUMN account_non_locked SET NOT NULL;
-- users account_non_expired字段设置为NOT NULL,添加false为默认值
ALTER TABLE users
    ALTER COLUMN account_non_expired SET DEFAULT false;
UPDATE users
SET account_non_expired = false
WHERE account_non_expired IS NULL;
ALTER TABLE users
    ALTER COLUMN account_non_expired SET NOT NULL;
-- users role_id字段设置为NOT NULL,添加0为默认值
ALTER TABLE users
    ALTER COLUMN role_id SET DEFAULT 0;
UPDATE users
SET role_id = 0
WHERE role_id IS NULL;
ALTER TABLE users
    ALTER COLUMN role_id SET NOT NULL;
-- users creation_time字段设置为NOT NULL,添加unix时间戳零点为默认值
ALTER TABLE users
    ALTER COLUMN creation_time SET DEFAULT '1970-01-01 00:00:00+00';
UPDATE users
SET creation_time = '1970-01-01 00:00:00+00'
WHERE creation_time IS NULL;
ALTER TABLE users
    ALTER COLUMN creation_time SET NOT NULL;
-- users modification_time字段设置为NOT NULL,添加unix时间戳零点为默认值
ALTER TABLE users
    ALTER COLUMN modification_time SET DEFAULT '1970-01-01 00:00:00+00';
UPDATE users
SET modification_time = '1970-01-01 00:00:00+00'
WHERE modification_time IS NULL;
ALTER TABLE users
    ALTER COLUMN modification_time SET NOT NULL;
-- users founder字段设置为NOT NULL,添加0为默认值
ALTER TABLE users
    ALTER COLUMN founder SET DEFAULT 0;
UPDATE users
SET founder = 0
WHERE founder IS NULL;
ALTER TABLE users
    ALTER COLUMN founder SET NOT NULL;
-- users modifier字段设置为NOT NULL,添加0为默认值
ALTER TABLE users
    ALTER COLUMN modifier SET DEFAULT 0;
UPDATE users
SET modifier = 0
WHERE modifier IS NULL;
ALTER TABLE users
    ALTER COLUMN modifier SET NOT NULL;
-- users sex字段设置为NOT NULL,添加SEXLESS为默认值
ALTER TABLE users
    ALTER COLUMN sex SET DEFAULT 'SEXLESS';
UPDATE users
SET sex = 'SEXLESS'
WHERE sex IS NULL;
ALTER TABLE users
    ALTER COLUMN sex SET NOT NULL;
-- users avatar_url字段设置为NOT NULL,添加空字符串为默认值
ALTER TABLE users
    ALTER COLUMN avatar_url SET DEFAULT '';
UPDATE users
SET avatar_url = ''
WHERE avatar_url IS NULL;
ALTER TABLE users
    ALTER COLUMN avatar_url SET NOT NULL;
-- users phone字段设置为NOT NULL,添加空字符串为默认值
ALTER TABLE users
    ALTER COLUMN phone SET DEFAULT '';
UPDATE users
SET phone = ''
WHERE phone IS NULL;
ALTER TABLE users
    ALTER COLUMN phone SET NOT NULL;
-- users email字段设置为NOT NULL,添加空字符串为默认值
ALTER TABLE users
    ALTER COLUMN email SET DEFAULT '';
UPDATE users
SET email = ''
WHERE email IS NULL;
ALTER TABLE users
    ALTER COLUMN email SET NOT NULL;
-- users timezone字段设置为NOT NULL,添加空字符串为默认值
ALTER TABLE users
    ALTER COLUMN timezone SET DEFAULT '';
UPDATE users
SET timezone = ''
WHERE timezone IS NULL;
ALTER TABLE users
    ALTER COLUMN timezone SET NOT NULL;
-- users language字段设置为NOT NULL,添加ZH为默认值
ALTER TABLE users
    ALTER COLUMN language SET DEFAULT 'ZH';
UPDATE users
SET language = 'ZH'
WHERE language IS NULL;
ALTER TABLE users
    ALTER COLUMN language SET NOT NULL;
