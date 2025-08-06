-- Add phone_number and email columns to companies table
ALTER TABLE companies 
ADD COLUMN phone_number VARCHAR(20),
ADD COLUMN email VARCHAR(255);

-- Add phone_number and email columns to business_unit table  
ALTER TABLE business_unit
ADD COLUMN phone_number VARCHAR(20),
ADD COLUMN email VARCHAR(255);

-- Add comments for documentation
COMMENT ON COLUMN companies.phone_number IS 'Phone number for the company (nullable)';
COMMENT ON COLUMN companies.email IS 'Email address for the company (nullable)';
COMMENT ON COLUMN business_unit.phone_number IS 'Phone number for the business unit (nullable)';
COMMENT ON COLUMN business_unit.email IS 'Email address for the business unit (nullable)';