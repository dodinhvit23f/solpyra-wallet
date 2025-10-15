-- Create sequence if not exists
CREATE SEQUENCE IF NOT EXISTS wallet_seq START 1 INCREMENT 1;

-- Create table if not exists
CREATE TABLE IF NOT EXISTS wallet (
    id BIGINT PRIMARY KEY DEFAULT nextval('wallet_seq'),
    customer_id BIGINT NOT NULL,
    balance NUMERIC(28,2) NOT NULL DEFAULT 0,
    withdraw_balance NUMERIC(28,2) NOT NULL DEFAULT 0,
    commissioned_balance NUMERIC(28,2) NOT NULL DEFAULT 0,
    version INTEGER NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    updated_by VARCHAR(255)
);