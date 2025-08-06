-- Fix timestamp to TIMESTAMPTZ conversion - Force all timestamp columns to TIMESTAMPTZ
-- This migration will definitely convert all TIMESTAMP columns to TIMESTAMPTZ

-- Enable necessary extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Force conversion of consumption_items table timestamps
-- Use explicit schema and table checks
DO $$ 
DECLARE
    rec RECORD;
BEGIN
    -- Check consumption_items table exists and get column info
    FOR rec IN 
        SELECT column_name, data_type 
        FROM information_schema.columns 
        WHERE table_schema = 'public' 
        AND table_name = 'consumption_items' 
        AND column_name IN ('created_at', 'updated_at')
        AND data_type = 'timestamp without time zone'
    LOOP
        EXECUTE format('ALTER TABLE consumption_items ALTER COLUMN %I TYPE TIMESTAMPTZ USING %I AT TIME ZONE CURRENT_SETTING(''timezone'')', 
                      rec.column_name, rec.column_name);
        RAISE NOTICE 'Converted consumption_items.% from TIMESTAMP to TIMESTAMPTZ', rec.column_name;
    END LOOP;
    
    -- Always set defaults regardless of current state
    ALTER TABLE consumption_items 
        ALTER COLUMN created_at SET DEFAULT CURRENT_TIMESTAMP,
        ALTER COLUMN updated_at SET DEFAULT CURRENT_TIMESTAMP;
        
    RAISE NOTICE 'Set defaults for consumption_items timestamp columns';
END $$;

-- Force conversion of consumption_records table timestamps
DO $$ 
DECLARE
    rec RECORD;
BEGIN
    -- Check consumption_records table exists and get column info
    FOR rec IN 
        SELECT column_name, data_type 
        FROM information_schema.columns 
        WHERE table_schema = 'public' 
        AND table_name = 'consumption_records' 
        AND column_name = 'consumed_at'
        AND data_type = 'timestamp without time zone'
    LOOP
        EXECUTE format('ALTER TABLE consumption_records ALTER COLUMN %I TYPE TIMESTAMPTZ USING %I AT TIME ZONE CURRENT_SETTING(''timezone'')', 
                      rec.column_name, rec.column_name);
        RAISE NOTICE 'Converted consumption_records.% from TIMESTAMP to TIMESTAMPTZ', rec.column_name;
    END LOOP;
    
    -- Always set default regardless of current state
    ALTER TABLE consumption_records 
        ALTER COLUMN consumed_at SET DEFAULT CURRENT_TIMESTAMP;
        
    RAISE NOTICE 'Set default for consumption_records.consumed_at';
END $$;

-- Create or replace function to automatically update the updated_at column
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

-- Drop and recreate trigger
DROP TRIGGER IF EXISTS update_consumption_items_updated_at ON consumption_items;
CREATE TRIGGER update_consumption_items_updated_at 
    BEFORE UPDATE ON consumption_items 
    FOR EACH ROW 
    EXECUTE FUNCTION update_updated_at_column();

-- Add comments explaining the timezone behavior
COMMENT ON COLUMN consumption_items.created_at IS 'Creation timestamp with timezone information (TIMESTAMPTZ)';
COMMENT ON COLUMN consumption_items.updated_at IS 'Last update timestamp with timezone information (TIMESTAMPTZ), auto-updated via trigger';
COMMENT ON COLUMN consumption_records.consumed_at IS 'Consumption timestamp with timezone information (TIMESTAMPTZ)';

-- Verify the conversion worked by showing current column types
DO $$ 
DECLARE
    rec RECORD;
BEGIN
    RAISE NOTICE 'Current column types after conversion:';
    FOR rec IN 
        SELECT table_name, column_name, data_type 
        FROM information_schema.columns 
        WHERE table_schema = 'public' 
        AND table_name IN ('consumption_items', 'consumption_records')
        AND column_name IN ('created_at', 'updated_at', 'consumed_at')
        ORDER BY table_name, column_name
    LOOP
        RAISE NOTICE '%.% = %', rec.table_name, rec.column_name, rec.data_type;
    END LOOP;
END $$;

-- Note: After this migration:
-- - All timestamp columns should be 'timestamp with time zone' (TIMESTAMPTZ)
-- - New records will automatically include timezone information
-- - You can insert with explicit timezone: '2024-01-15 14:30:00+01'
-- - PostgreSQL will display timezone offsets when querying