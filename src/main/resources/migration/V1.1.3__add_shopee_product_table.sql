-- Create sequence if not exists
CREATE SEQUENCE IF NOT EXISTS commission_log_id_seq START 1 INCREMENT 1;

-- Create table if not exists
CREATE TABLE IF NOT EXISTS commission_log (
    id BIGINT PRIMARY KEY DEFAULT nextval('commission_log_id_seq'),
    order_id BIGINT NOT NULL,
    wallet_id BIGINT NOT NULL,
    commission_amount NUMERIC(18,2) NOT NULL,
    status SMALLINT NOT NULL,
    processed_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),
    version INTEGER NOT NULL DEFAULT 0,
    );

-- Optional: Add index
CREATE INDEX IF NOT EXISTS idx_commission_log_wallet_id ON commission_log(wallet_id);