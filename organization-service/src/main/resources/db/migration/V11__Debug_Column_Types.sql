-- Debug migration to show current column types and test TIMESTAMPTZ functionality

-- Show all timestamp-related columns in our tables
DO $$ 
DECLARE
    rec RECORD;
BEGIN
    RAISE NOTICE '=== CURRENT COLUMN TYPES ===';
    FOR rec IN 
        SELECT 
            t.table_name,
            c.column_name, 
            c.data_type,
            c.column_default,
            c.is_nullable
        FROM information_schema.tables t
        JOIN information_schema.columns c ON t.table_name = c.table_name
        WHERE t.table_schema = 'public' 
        AND t.table_type = 'BASE TABLE'
        AND c.table_schema = 'public'
        AND (c.data_type LIKE '%timestamp%' OR c.column_name LIKE '%_at')
        ORDER BY t.table_name, c.column_name
    LOOP
        RAISE NOTICE 'Table: % | Column: % | Type: % | Default: % | Nullable: %', 
                     rec.table_name, rec.column_name, rec.data_type, 
                     COALESCE(rec.column_default, 'NULL'), rec.is_nullable;
    END LOOP;
END $$;

-- Test inserting a record with explicit timezone to see if TIMESTAMPTZ is working
DO $$ 
BEGIN
    -- Try to insert a test record with timezone information
    -- This will only work if the columns are actually TIMESTAMPTZ
    RAISE NOTICE '=== TESTING TIMESTAMPTZ FUNCTIONALITY ===';
    
    -- Test if we can insert with timezone offset
    BEGIN
        -- This should work if created_at is TIMESTAMPTZ
        INSERT INTO consumption_items (id, name, price, type, business_unit_id, created_at, updated_at) 
        VALUES ('test-tz-uuid-' || extract(epoch from now()), 'Timezone Test Item', 1.00, 'test', 
                (SELECT id FROM business_unit LIMIT 1), 
                '2024-01-15 14:30:00+02'::TIMESTAMPTZ,
                '2024-01-15 14:30:00+02'::TIMESTAMPTZ);
        
        RAISE NOTICE 'SUCCESS: Inserted item with explicit timezone +02';
        
        -- Clean up test record
        DELETE FROM consumption_items WHERE name = 'Timezone Test Item';
        
    EXCEPTION WHEN OTHERS THEN
        RAISE NOTICE 'FAILED: Could not insert with timezone - %', SQLERRM;
    END;
END $$;

-- Show timezone settings
DO $$ 
BEGIN
    RAISE NOTICE '=== TIMEZONE SETTINGS ===';
    RAISE NOTICE 'Current timezone setting: %', CURRENT_SETTING('timezone');
    RAISE NOTICE 'Current timestamp: %', CURRENT_TIMESTAMP;
    RAISE NOTICE 'Current timestamp with timezone: %', NOW();
END $$;