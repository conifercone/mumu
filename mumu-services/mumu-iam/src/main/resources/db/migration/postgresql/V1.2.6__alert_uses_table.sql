-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
ALTER TABLE users ALTER COLUMN sex type varchar(100) using sex::varchar;
ALTER TABLE users ALTER COLUMN sex set default 'SEXLESS';
ALTER TABLE users ALTER COLUMN language type varchar(100) using language::varchar;
ALTER TABLE users ALTER COLUMN language set default 'ZH';
ALTER TABLE users_archived ALTER COLUMN sex type varchar(100) using sex::varchar;
ALTER TABLE users_archived ALTER COLUMN sex set default 'SEXLESS';
ALTER TABLE users_archived ALTER COLUMN language type varchar(100) using language::varchar;
ALTER TABLE users_archived ALTER COLUMN language set default 'ZH';
drop type if exists sex;
drop type if exists language;
