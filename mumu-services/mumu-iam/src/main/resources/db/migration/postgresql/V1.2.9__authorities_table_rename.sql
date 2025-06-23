-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
-- 1. 修改权限表（authorities）为 permissions
ALTER TABLE public.authorities RENAME TO permissions;

-- 修改索引名
ALTER INDEX authorities_code_gin_index RENAME TO permissions_code_gin_index;
ALTER INDEX authorities_name_gin_index RENAME TO permissions_name_gin_index;

-- 修改约束名
ALTER TABLE public.permissions RENAME CONSTRAINT authorities_unique_code TO permissions_unique_code;
ALTER TABLE public.permissions RENAME CONSTRAINT authorities_pk_id TO permissions_pk_id;

-- 2. 修改权限归档表（authorities_archived）为 permissions_archived
ALTER TABLE public.authorities_archived RENAME TO permissions_archived;

-- 修改索引名
ALTER INDEX authorities_archived_code_gin_index RENAME TO permissions_archived_code_gin_index;
ALTER INDEX authorities_archived_name_gin_index RENAME TO permissions_archived_name_gin_index;

-- 修改约束名
ALTER TABLE public.permissions_archived RENAME CONSTRAINT authorities_archived_unique_code TO permissions_archived_unique_code;
ALTER TABLE public.permissions_archived RENAME CONSTRAINT authorities_archived_pk_id TO permissions_archived_pk_id;

-- 3. 修改权限路径表（authority_paths）为 permission_paths
ALTER TABLE public.authority_paths RENAME TO permission_paths;

-- 修改索引名
ALTER INDEX authority_paths_ancestor_id RENAME TO permission_paths_ancestor_id;
ALTER INDEX authority_paths_depth RENAME TO permission_paths_depth;
ALTER INDEX authority_paths_descendant_id RENAME TO permission_paths_descendant_id;

-- 修改约束名
ALTER TABLE public.permission_paths RENAME CONSTRAINT authority_paths_pkey TO permission_paths_pkey;

-- 4. 修改角色权限关系表（role_authorities）为 role_permissions
ALTER TABLE public.role_authorities RENAME TO role_permissions;

-- 修改相关的列名
ALTER TABLE public.role_permissions RENAME COLUMN authority_id TO permission_id;

-- 修改索引名
ALTER INDEX role_authorities_role_id RENAME TO role_permissions_role_id;
ALTER INDEX role_authorities_authority_id RENAME TO role_permissions_permission_id;

-- 修改约束名
ALTER TABLE public.role_permissions RENAME CONSTRAINT role_authorities_pkey TO role_permissions_pkey;

