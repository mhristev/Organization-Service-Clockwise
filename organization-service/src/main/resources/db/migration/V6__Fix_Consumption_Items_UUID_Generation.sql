-- Fix consumption_items table to auto-generate UUIDs
-- This migration fixes the ID generation issue for consumption items

-- First, check if the table has any data and handle accordingly
-- For PostgreSQL, we need to use UUID extension and set default UUID generation

-- Enable UUID extension if not already enabled
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

-- Drop the existing primary key constraint
ALTER TABLE consumption_items DROP CONSTRAINT consumption_items_pkey;

-- Change the id column type to VARCHAR(50) to match other tables and set UUID generation
ALTER TABLE consumption_items ALTER COLUMN id TYPE VARCHAR(50);
-- Modify the id column to auto-generate UUIDs as text (to match other tables)
ALTER TABLE consumption_items ALTER COLUMN id SET DEFAULT uuid_generate_v4()::text;

-- Add the primary key constraint back
ALTER TABLE consumption_items ADD CONSTRAINT consumption_items_pkey PRIMARY KEY (id);
