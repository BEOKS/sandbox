-- V2: Add phone column to users table
ALTER TABLE users ADD COLUMN phone VARCHAR(20);

-- Update existing records
UPDATE users SET phone = '010-1234-5678' WHERE email = 'john@example.com';
UPDATE users SET phone = '010-8765-4321' WHERE email = 'jane@example.com';
