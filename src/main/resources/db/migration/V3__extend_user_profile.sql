-- Extend users table with additional profile fields for hybrid pattern
ALTER TABLE users
    ADD COLUMN IF NOT EXISTS dni VARCHAR(50),
    ADD COLUMN IF NOT EXISTS phone VARCHAR(30),
    ADD COLUMN IF NOT EXISTS profession VARCHAR(100);

-- Ensure DNI is unique when present (PostgreSQL partial unique index)
CREATE UNIQUE INDEX IF NOT EXISTS uk_users_dni_not_null ON users (dni) WHERE dni IS NOT NULL;