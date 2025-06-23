-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
ALTER TABLE permission_paths RENAME TO mumu_permission_paths;
ALTER TABLE permissions RENAME TO mumu_permissions;
ALTER TABLE permissions_archived RENAME TO mumu_permissions_archived;
ALTER TABLE role_paths RENAME TO mumu_role_paths;
ALTER TABLE role_permissions RENAME TO mumu_role_permissions;
ALTER TABLE roles RENAME TO mumu_roles;
ALTER TABLE roles_archived RENAME TO mumu_roles_archived;
ALTER TABLE user_roles RENAME TO mumu_user_roles;
ALTER TABLE users RENAME TO mumu_users;
ALTER TABLE users_archived RENAME TO mumu_users_archived;
