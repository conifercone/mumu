-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
-- 权限 & 角色 增加 description 字段用于存储权限 & 角色描述信息
ALTER TABLE permissions
    ADD COLUMN description varchar(500) NOT NULL DEFAULT '';
ALTER TABLE roles
    ADD COLUMN description varchar(500) NOT NULL DEFAULT '';
ALTER TABLE permissions_archived
    ADD COLUMN description varchar(500) NOT NULL DEFAULT '';
ALTER TABLE roles_archived
    ADD COLUMN description varchar(500) NOT NULL DEFAULT '';
