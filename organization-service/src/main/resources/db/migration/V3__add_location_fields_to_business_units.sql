-- V3__add_location_fields_to_business_units.sql
-- Adds latitude, longitude, and allowed_radius columns to business_unit table

ALTER TABLE business_unit 
ADD COLUMN latitude DOUBLE PRECISION,
ADD COLUMN longitude DOUBLE PRECISION,
ADD COLUMN allowed_radius DOUBLE PRECISION DEFAULT 200.0;

-- Add comments for clarity
COMMENT ON COLUMN business_unit.latitude IS 'Latitude coordinate of the business unit location in decimal degrees';
COMMENT ON COLUMN business_unit.longitude IS 'Longitude coordinate of the business unit location in decimal degrees';
COMMENT ON COLUMN business_unit.allowed_radius IS 'Allowed radius in meters for clock-in location validation (default: 200m)';
