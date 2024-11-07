-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
CREATE TABLE authority_paths (
                            ancestor_id BIGINT NOT NULL,
                            descendant_id BIGINT NOT NULL,
                            depth BIGINT NOT NULL,
                            creation_time     timestamp with time zone default '1970-01-01 00:00:00+00'::timestamp with time zone not null,
                            founder           bigint                   default 0                                                  not null,
                            modifier          bigint                   default 0                                                  not null,
                            modification_time timestamp with time zone default '1970-01-01 00:00:00+00'::timestamp with time zone not null,
                            PRIMARY KEY (ancestor_id, descendant_id)
);
CREATE INDEX authority_paths_ancestor_id ON authority_paths(ancestor_id);
CREATE INDEX authority_paths_descendant_id ON authority_paths(descendant_id);
comment on table authority_paths is '权限路径表';
comment on column authority_paths.ancestor_id is '祖先ID';
comment on column authority_paths.descendant_id is '后代ID';
comment on column authority_paths.depth is '路径深度';
comment on column authority_paths.creation_time is '创建时间';
comment on column authority_paths.founder is '创建人';
comment on column authority_paths.modifier is '修改人';
comment on column authority_paths.modification_time is '修改时间';
