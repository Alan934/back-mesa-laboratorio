ALTER TABLE appointments
    ADD COLUMN IF NOT EXISTS practitioner_id UUID NULL REFERENCES users(id) ON DELETE SET NULL;

CREATE INDEX IF NOT EXISTS idx_appointments_practitioner_id ON appointments(practitioner_id);