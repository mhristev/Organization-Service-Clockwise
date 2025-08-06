-- Convert all TIMESTAMP columns to TIMESTAMPTZ to preserve timezone information
-- This allows storing timestamps with their timezone offsets (+01, +02, etc.)
-- Examples: '2024-01-15 10:30:00+02', '2024-01-15 08:30:00+00', '2024-01-15 11:30:00+03'

-- Update consumption_items table timestamps to TIMESTAMPTZ
ALTER TABLE consumption_items 
    ALTER COLUMN created_at TYPE TIMESTAMPTZ USING created_at AT TIME ZONE 'UTC',
    ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP,
    ALTER COLUMN updated_at TYPE TIMESTAMPTZ USING updated_at AT TIME ZONE 'UTC',
    ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;

-- Update consumption_records table timestamps to TIMESTAMPTZ
-- Note: This table was created in V7 with TIMESTAMP, so we need to convert it
ALTER TABLE consumption_records 
    ALTER COLUMN consumed_at TYPE TIMESTAMPTZ USING consumed_at AT TIME ZONE 'UTC',
    ALTER COLUMN consumed_at SET DEFAULT CURRENT_TIMESTAMP;

-- Add a function to automatically update the updated_at column for consumption_items
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

-- Create trigger to automatically update updated_at on consumption_items
CREATE TRIGGER update_consumption_items_updated_at 
    BEFORE UPDATE ON consumption_items 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Note: TIMESTAMPTZ will store timestamps with timezone information
-- When inserting, you can specify timezone: '2024-01-15 10:30:00+02'
-- PostgreSQL will store it internally in UTC but display with timezone info
-- CURRENT_TIMESTAMP will use the session's timezone setting