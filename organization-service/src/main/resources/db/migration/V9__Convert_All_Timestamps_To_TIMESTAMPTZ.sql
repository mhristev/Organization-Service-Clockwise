-- Convert all TIMESTAMP columns to TIMESTAMPTZ across all tables
-- This migration systematically converts all timestamp columns to preserve timezone information
-- Examples of timezone-aware timestamps: '2024-01-15 10:30:00+02', '2024-01-15 08:30:00+00', '2024-01-15 11:30:00+03'

-- Enable necessary extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- First, identify and convert all TIMESTAMP columns to TIMESTAMPTZ
-- This query-based approach ensures we catch all timestamp columns

-- Convert consumption_items table timestamps (only if they are not already TIMESTAMPTZ)
-- These columns were created in V5 with TIMESTAMP type
DO $$ 
BEGIN
    -- Check and convert created_at column
    IF (SELECT data_type FROM information_schema.columns 
        WHERE table_name = 'consumption_items' AND column_name = 'created_at') = 'timestamp without time zone' THEN
        ALTER TABLE consumption_items 
            ALTER COLUMN created_at TYPE TIMESTAMPTZ USING created_at AT TIME ZONE CURRENT_SETTING('timezone');
    END IF;
    
    -- Set default for created_at
    ALTER TABLE consumption_items 
        ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP;
    
    -- Check and convert updated_at column
    IF (SELECT data_type FROM information_schema.columns 
        WHERE table_name = 'consumption_items' AND column_name = 'updated_at') = 'timestamp without time zone' THEN
        ALTER TABLE consumption_items 
            ALTER COLUMN updated_at TYPE TIMESTAMPTZ USING updated_at AT TIME ZONE CURRENT_SETTING('timezone');
    END IF;
    
    -- Set default for updated_at
    ALTER TABLE consumption_items 
        ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;
END $$;

-- Convert consumption_records table timestamps (only if they are not already TIMESTAMPTZ)
-- This column was created in V7 with TIMESTAMP type
DO $$ 
BEGIN
    -- Check and convert consumed_at column
    IF (SELECT data_type FROM information_schema.columns 
        WHERE table_name = 'consumption_records' AND column_name = 'consumed_at') = 'timestamp without time zone' THEN
        ALTER TABLE consumption_records 
            ALTER COLUMN consumed_at TYPE TIMESTAMPTZ USING consumed_at AT TIME ZONE CURRENT_SETTING('timezone');
    END IF;
    
    -- Set default for consumed_at
    ALTER TABLE consumption_records 
        ALTER COLUMN consumed_at SET DEFAULT CURRENT_TIMESTAMP;
END $$;

-- Create or replace function to automatically update the updated_at column
-- This ensures updated_at is always set to current timestamp when a row is modified
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Drop trigger if it exists, then create it
-- This handles cases where the trigger might already exist from previous migrations
DROP TRIGGER IF EXISTS update_consumption_items_updated_at ON consumption_items;
CREATE TRIGGER update_consumption_items_updated_at 
    BEFORE UPDATE ON consumption_items 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Add comment explaining the timezone behavior
COMMENT ON COLUMN consumption_items.created_at IS 'Creation timestamp with timezone information (TIMESTAMPTZ)';
COMMENT ON COLUMN consumption_items.updated_at IS 'Last update timestamp with timezone information (TIMESTAMPTZ), auto-updated via trigger';
COMMENT ON COLUMN consumption_records.consumed_at IS 'Consumption timestamp with timezone information (TIMESTAMPTZ)';

-- Note: TIMESTAMPTZ behavior
-- - Stores timestamps with timezone information
-- - Internally converts to UTC for storage
-- - Displays with original timezone offset when queried
-- - CURRENT_TIMESTAMP uses the session/server timezone setting
-- - Can insert with explicit timezone: INSERT INTO table (timestamp_col) VALUES ('2024-01-15 14:30:00+01');
-- - PostgreSQL will automatically handle timezone conversions for comparisons and calculations