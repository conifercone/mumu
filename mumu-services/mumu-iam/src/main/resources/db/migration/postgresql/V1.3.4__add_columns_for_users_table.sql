-- @formatter:off
-- noinspection SqlConstantExpressionForFile
-- noinspection SqlResolveForFile
ALTER TABLE users
    ADD COLUMN balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE users
    ADD COLUMN balance_currency varchar(10) NOT NULL DEFAULT 'USD';
ALTER TABLE users_archived
    ADD COLUMN balance DECIMAL(15, 2) NOT NULL DEFAULT 0.00;
ALTER TABLE users_archived
    ADD COLUMN balance_currency varchar(10) NOT NULL DEFAULT 'USD';
comment on column users.balance is '余额';
comment on column users.balance_currency is '余额货币';
comment on column users_archived.balance is '余额';
comment on column users_archived.balance_currency is '余额货币';
