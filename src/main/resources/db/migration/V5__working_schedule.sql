CREATE TABLE IF NOT EXISTS working_days (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    practitioner_id UUID NOT NULL,
    day_of_week VARCHAR(16) NOT NULL,
    CONSTRAINT fk_working_day_practitioner FOREIGN KEY (practitioner_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT uk_practitioner_day UNIQUE (practitioner_id, day_of_week)
);

CREATE TABLE IF NOT EXISTS working_intervals (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    working_day_id UUID NOT NULL,
    start_time TIME NOT NULL,
    end_time TIME NOT NULL,
    CONSTRAINT fk_interval_day FOREIGN KEY (working_day_id) REFERENCES working_days(id) ON DELETE CASCADE
);

CREATE INDEX IF NOT EXISTS idx_working_days_practitioner ON working_days(practitioner_id);
CREATE INDEX IF NOT EXISTS idx_working_intervals_day ON working_intervals(working_day_id);
