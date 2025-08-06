-- Create consumption_records table to track consumption during work sessions
-- Enable UUID extension if not already enabled
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

CREATE TABLE consumption_records (
    id VARCHAR(50) PRIMARY KEY DEFAULT uuid_generate_v4()::text,
    consumption_item_id VARCHAR(50) NOT NULL,
    user_id VARCHAR(255) NOT NULL,
    work_session_id VARCHAR(255) NOT NULL,
    quantity DECIMAL(10,2) NOT NULL CHECK (quantity > 0),
    consumed_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_consumption_records_consumption_item 
        FOREIGN KEY (consumption_item_id) 
        REFERENCES consumption_items(id) 
        ON DELETE CASCADE
);

-- Create indexes for better query performance
CREATE INDEX idx_consumption_records_consumption_item_id ON consumption_records(consumption_item_id);
CREATE INDEX idx_consumption_records_user_id ON consumption_records(user_id);
CREATE INDEX idx_consumption_records_work_session_id ON consumption_records(work_session_id);
CREATE INDEX idx_consumption_records_consumed_at ON consumption_records(consumed_at);

-- Create composite index for common queries
CREATE INDEX idx_consumption_records_item_user_session ON consumption_records(consumption_item_id, user_id, work_session_id);