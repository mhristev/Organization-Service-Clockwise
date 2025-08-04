-- Create consumption_items table
-- This table stores consumption items (food, drinks, etc.) for each business unit
CREATE TABLE consumption_items (
    id VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    price DECIMAL(10,2) NOT NULL,
    type VARCHAR(255) NOT NULL,
    business_unit_id VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (business_unit_id) REFERENCES business_unit(id) ON DELETE CASCADE
);

-- Add index for business_unit_id for better query performance
CREATE INDEX idx_consumption_items_business_unit_id ON consumption_items(business_unit_id);

-- Add index for type for filtering by type
CREATE INDEX idx_consumption_items_type ON consumption_items(type);
