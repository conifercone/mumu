-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
CREATE TABLE role_paths (
                            ancestor_id BIGINT NOT NULL,
                            descendant_id BIGINT NOT NULL,
                            depth BIGINT NOT NULL,
                            creation_time     timestamp with time zone default '1970-01-01 00:00:00+00'::timestamp with time zone not null,
                            founder           bigint                   default 0                                                  not null,
                            modifier          bigint                   default 0                                                  not null,
                            modification_time timestamp with time zone default '1970-01-01 00:00:00+00'::timestamp with time zone not null,
                            PRIMARY KEY (ancestor_id, descendant_id,depth)
);
CREATE INDEX role_paths_ancestor_id ON role_paths(ancestor_id);
CREATE INDEX role_paths_descendant_id ON role_paths(descendant_id);
CREATE INDEX role_paths_depth ON role_paths(depth);
comment on table role_paths is '角色路径表';
comment on column role_paths.ancestor_id is '祖先ID';
comment on column role_paths.descendant_id is '后代ID';
comment on column role_paths.depth is '路径深度';
comment on column role_paths.creation_time is '创建时间';
comment on column role_paths.founder is '创建人';
comment on column role_paths.modifier is '修改人';
comment on column role_paths.modification_time is '修改时间';
INSERT INTO role_paths (ancestor_id, descendant_id, depth, creation_time, founder, modifier, modification_time) VALUES (1,1,  0, now(), 0, 0, now()) ON CONFLICT (ancestor_id,descendant_id,depth)  DO NOTHING;
INSERT INTO role_paths (ancestor_id, descendant_id, depth, creation_time, founder, modifier, modification_time) VALUES (2,2,  0, now(), 0, 0, now()) ON CONFLICT (ancestor_id,descendant_id,depth)  DO NOTHING;
INSERT INTO role_paths (ancestor_id, descendant_id, depth, creation_time, founder, modifier, modification_time) VALUES (0,0,  0, now(), 0, 0, now()) ON CONFLICT (ancestor_id,descendant_id,depth)  DO NOTHING;
