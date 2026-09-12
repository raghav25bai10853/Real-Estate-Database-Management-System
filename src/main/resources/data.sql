-- ===================================================================
-- Real Estate Management System - data.sql
-- Runs automatically after schema.sql on every startup.
-- Written so re-running it on every restart will NOT duplicate data.
-- ===================================================================

-- Default admin login: username = admin, password = admin123
-- MERGE ... KEY(username) = "insert if not exists, otherwise update" (upsert)
MERGE INTO admins (username, password) KEY(username) VALUES ('admin', 'admin123');

-- Seed sample properties ONLY the very first time (table is empty).
INSERT INTO properties (title, description, city, address, type, price, bedrooms, bathrooms, area_sqft, status, created_at)
SELECT * FROM (
    VALUES
    ('Sunrise 2BHK Apartment', 'Cozy apartment near main market', 'Bhopal', 'MP Nagar, Bhopal', 'APARTMENT', 4500000.00, 2, 2, 950.0, 'AVAILABLE', CURRENT_TIMESTAMP),
    ('Green Valley Villa', 'Spacious 4BHK villa with garden', 'Bhopal', 'Kolar Road, Bhopal', 'VILLA', 12500000.00, 4, 4, 3200.0, 'AVAILABLE', CURRENT_TIMESTAMP),
    ('Riverside Plot', 'Open residential plot near riverside', 'Indore', 'Rau, Indore', 'PLOT', 2500000.00, NULL, NULL, 2000.0, 'AVAILABLE', CURRENT_TIMESTAMP),
    ('Downtown Commercial Space', 'Prime commercial shop space', 'Indore', 'Vijay Nagar, Indore', 'COMMERCIAL', 8500000.00, NULL, 2, 1500.0, 'AVAILABLE', CURRENT_TIMESTAMP),
    ('Lakeview 3BHK House', 'Independent house with lake view', 'Bhopal', 'Shahpura, Bhopal', 'HOUSE', 9000000.00, 3, 3, 2100.0, 'SOLD', CURRENT_TIMESTAMP),
    ('Metro Heights Apartment', '1BHK apartment close to metro station', 'Delhi', 'Dwarka, Delhi', 'APARTMENT', 3800000.00, 1, 1, 600.0, 'RENTED', CURRENT_TIMESTAMP)
) AS seed_data
WHERE NOT EXISTS (SELECT 1 FROM properties);

-- Seed one sample inquiry ONLY the very first time (table is empty).
INSERT INTO inquiries (property_id, name, email, phone, message, created_at)
SELECT 1, 'Rahul Sharma', 'rahul.sharma@example.com', '9876543210',
       'Interested in this apartment. Please share more photos.', CURRENT_TIMESTAMP
WHERE NOT EXISTS (SELECT 1 FROM inquiries)
  AND EXISTS (SELECT 1 FROM properties WHERE id = 1);
