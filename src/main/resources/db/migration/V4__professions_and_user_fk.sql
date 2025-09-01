CREATE TABLE IF NOT EXISTS professions (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT now(),
    name VARCHAR(100) NOT NULL UNIQUE
);

ALTER TABLE users ADD COLUMN IF NOT EXISTS profession_id UUID;

-- Backfill distinct professions from old string column, if exists
DO $$
BEGIN
    IF EXISTS (
        SELECT 1
        FROM information_schema.columns
        WHERE table_name = 'users' AND column_name = 'profession'
    ) THEN
        INSERT INTO professions (name)
        SELECT DISTINCT profession
        FROM users
        WHERE profession IS NOT NULL AND profession <> ''
        ON CONFLICT (name) DO NOTHING;

        UPDATE users u
        SET profession_id = p.id
        FROM professions p
        WHERE u.profession IS NOT NULL AND u.profession <> '' AND LOWER(p.name) = LOWER(u.profession);

        -- Drop old column
        ALTER TABLE users DROP COLUMN IF EXISTS profession;
    END IF;
END $$;

ALTER TABLE users
    ADD CONSTRAINT fk_users_profession FOREIGN KEY (profession_id) REFERENCES professions(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_users_profession_id ON users(profession_id);
