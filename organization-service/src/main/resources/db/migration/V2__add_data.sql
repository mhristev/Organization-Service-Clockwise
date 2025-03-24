-- Insert companies first
    INSERT INTO companies (name, description) VALUES
        ('Tech Corp', 'Technology solutions provider'),
        ('Food Services Inc', 'Restaurant chain management'),
        ('Retail Solutions', 'Retail business management');

    -- Insert business units
    INSERT INTO business_unit (name, location, description, company_id) VALUES
        ('Tech Corp HQ', 'San Francisco', 'Main headquarters', (SELECT id FROM companies WHERE name = 'Tech Corp')),
        ('Tech Corp R&D', 'Boston', 'Research facility', (SELECT id FROM companies WHERE name = 'Tech Corp')),
        ('Downtown Kitchen', 'New York', 'Fine dining restaurant', (SELECT id FROM companies WHERE name = 'Food Services Inc')),
        ('Mall Store #1', 'Chicago', 'Flagship retail store', (SELECT id FROM companies WHERE name = 'Retail Solutions'));