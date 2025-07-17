-- V4__populate_location_coordinates.sql
-- Updates existing business units with real latitude and longitude coordinates

-- Update Tech Corp HQ (San Francisco - Using coordinates for downtown SF)
UPDATE business_unit 
SET latitude = 37.7749, longitude = -122.4194, allowed_radius = 200.0
WHERE name = 'Tech Corp HQ';

-- Update Tech Corp R&D (Boston - Using coordinates for downtown Boston)
UPDATE business_unit 
SET latitude = 42.3601, longitude = -71.0589, allowed_radius = 150.0
WHERE name = 'Tech Corp R&D';

-- Update Downtown Kitchen (New York - Using coordinates for downtown NYC)
UPDATE business_unit 
SET latitude = 40.7128, longitude = -74.0060, allowed_radius = 100.0
WHERE name = 'Downtown Kitchen';

-- Update Mall Store #1 (Chicago - Using coordinates for downtown Chicago)
UPDATE business_unit 
SET latitude = 41.8781, longitude = -87.6298, allowed_radius = 250.0
WHERE name = 'Mall Store #1';