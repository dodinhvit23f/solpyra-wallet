-- Sequence for primary key
CREATE SEQUENCE IF NOT EXISTS payout_cycle_seq START 1 INCREMENT 1;

-- Payout cycle table
CREATE TABLE IF NOT EXISTS payout_cycle (
    id BIGINT PRIMARY KEY DEFAULT nextval('payout_cycle_seq'),
    wallet_id BIGINT NOT NULL,
    amount
    period_year INT NOT NULL,
    period_month INT NOT NULL,
    start_at TIMESTAMPTZ NOT NULL,
    end_at   TIMESTAMPTZ NOT NULL,
    status varchar(20) NOT NULL DEFAULT 'OPEN',
    closed_at TIMESTAMPTZ,
    paid_image VARCHAR(1000),
    version INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_payout_cycle_wallet FOREIGN KEY (wallet_id) REFERENCES wallet(id),

    CONSTRAINT chk_payout_cycle_month CHECK (period_month BETWEEN 1 AND 12),
    CONSTRAINT chk_payout_cycle_range CHECK (end_at > start_at)
    );

-- Unique: 1 wallet chỉ có 1 chu kỳ mỗi tháng
CREATE UNIQUE INDEX IF NOT EXISTS uq_payout_cycle_wallet_month
    ON payout_cycle(wallet_id, period_year, period_month);

CREATE INDEX IF NOT EXISTS idx_payout_cycle_status ON payout_cycle(status);