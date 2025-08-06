-- Add user first name and last name columns to consumption_records table
-- These will be populated via Kafka communication with the User Service

-- Add user name columns to consumption_records
ALTER TABLE consumption_records 
    ADD COLUMN user_first_name VARCHAR(255),
    ADD COLUMN user_last_name VARCHAR(255);

-- Add indexes for better query performance on user names
CREATE INDEX idx_consumption_records_user_first_name ON consumption_records(user_first_name);
CREATE INDEX idx_consumption_records_user_last_name ON consumption_records(user_last_name);

-- Add composite index for full name searches
CREATE INDEX idx_consumption_records_user_full_name ON consumption_records(user_first_name, user_last_name);

-- Add comments explaining the purpose of these columns
COMMENT ON COLUMN consumption_records.user_first_name IS 'User first name fetched from User Service via Kafka';
COMMENT ON COLUMN consumption_records.user_last_name IS 'User last name fetched from User Service via Kafka';

-- Note: These columns will be populated asynchronously via Kafka communication
-- The Organization Service will:
-- 1. Send user-info-request to User Service with user_id
-- 2. Receive user-info-response from User Service with user details
-- 3. Update the consumption record with the user names