-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
ALTER TABLE mumu_users
    ADD COLUMN phone_country_code varchar(10) NOT NULL DEFAULT '';
ALTER TABLE mumu_users_archived
    ADD COLUMN phone_country_code varchar(10) NOT NULL DEFAULT '';
comment on column mumu_users.phone_country_code is '国际电话区号';
comment on column mumu_users_archived.phone_country_code is '国际电话区号';
