-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
ALTER TABLE mumu_users
    ALTER COLUMN birthday SET DEFAULT CURRENT_DATE;
ALTER TABLE mumu_users_archived
    ALTER COLUMN birthday SET DEFAULT CURRENT_DATE;
