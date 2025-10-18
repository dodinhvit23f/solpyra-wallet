-- Create sequence if not exists
CREATE SEQUENCE IF NOT EXISTS wallet_transaction_seq START 1 INCREMENT 1;

-- Create table if not exists
CREATE TABLE IF NOT EXISTS wallet_transaction (
    id BIGINT PRIMARY KEY DEFAULT nextval('wallet_transaction_seq'),
    wallet_id BIGINT NOT NULL,
    amount NUMERIC(28,2) NOT NULL,
    transaction_type SMALLINT NOT NULL, -- e.g., 'COMMISSION', 'WITHDRAW'
    reference_id BIGINT, -- orderId or other external reference
    payment_image VARCHAR(1500),
    version INTEGER NOT NULL DEFAULT 0,
    created_date TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    created_by VARCHAR(255),
    updated_date TIMESTAMPTZ DEFAULT NOW(),
    updated_by VARCHAR(255)
 );

-- Optional: Add index for performance
CREATE INDEX IF NOT EXISTS idx_wallet_transaction_wallet_id ON wallet_transaction(wallet_id);
CREATE INDEX IF NOT EXISTS idx_wallet_transaction_ref ON wallet_transaction(reference_id);