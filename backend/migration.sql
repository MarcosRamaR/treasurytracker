-- ============================================================
-- Migración: Expenses/Incomes separadas → Transaction unificada
-- Ejecutar en la base de datos: treasury
-- Antes de arrancar los servicios
-- ============================================================

BEGIN;

-- 1. Crear tabla transactions
CREATE TABLE IF NOT EXISTS transactions (
    id BIGSERIAL PRIMARY KEY,
    amount NUMERIC(19,4) NOT NULL,
    description VARCHAR(500) NOT NULL,
    category VARCHAR(100),
    date DATE NOT NULL,
    user_id BIGINT NOT NULL,
    type VARCHAR(20) NOT NULL,
    applicated_at_balance BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP,
    version BIGINT NOT NULL DEFAULT 0
);

-- 2. Migrar datos de expenses → transactions
INSERT INTO transactions (
    id, amount, description, category, date, user_id,
    type, applicated_at_balance, created_at, updated_at, version
)
SELECT
    id, amount, description, category, date, user_id,
    'EXPENSE',
    applicated_at_balance,
    created_at,
    updated_at,
    0
FROM expenses
ON CONFLICT (id) DO NOTHING;

-- 3. Migrar datos de incomes → transactions
INSERT INTO transactions (
    id, amount, description, category, date, user_id,
    type, applicated_at_balance, created_at, updated_at, version
)
SELECT
    id, amount, description, category, date, user_id,
    'INCOME',
    applicated_at_balance,
    created_at,
    updated_at,
    0
FROM incomes
ON CONFLICT (id) DO NOTHING;

-- 4. Resetear secuencia
SELECT setval('transactions_id_seq', COALESCE(MAX(id), 0) + 1, false) FROM transactions;

-- 5. Índices
CREATE INDEX IF NOT EXISTS idx_transactions_user_date ON transactions(user_id, date DESC);
CREATE INDEX IF NOT EXISTS idx_transactions_user_applicated ON transactions(user_id, applicated_at_balance);
CREATE INDEX IF NOT EXISTS idx_transactions_user_type ON transactions(user_id, type);
CREATE INDEX IF NOT EXISTS idx_transactions_date ON transactions(date);

COMMIT;