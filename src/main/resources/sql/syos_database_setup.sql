-- SYOS Database Setup Script for XAMPP MySQL
-- Run this script in phpMyAdmin or MySQL command line

-- Create the database
CREATE DATABASE IF NOT EXISTS syos_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

-- Use the database
USE syos_db;

-- Create the database user (run this as root)
-- Note: You may need to run this separately if you don't have CREATE USER privileges
CREATE USER IF NOT EXISTS 'syos_user'@'localhost' IDENTIFIED BY 'temp1234';

-- Grant all privileges on the database to the user
GRANT ALL PRIVILEGES ON syos_db.* TO 'syos_user'@'localhost';

-- Also grant for % (any host) if needed for remote connections
CREATE USER IF NOT EXISTS 'syos_user'@'%' IDENTIFIED BY 'temp1234';
GRANT ALL PRIVILEGES ON syos_db.* TO 'syos_user'@'%';

-- Flush privileges to apply changes
FLUSH PRIVILEGES;

-- Create a test table to verify everything works
CREATE TABLE IF NOT EXISTS connection_test (
    id INT AUTO_INCREMENT PRIMARY KEY,
    test_message VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Insert some test data
INSERT INTO connection_test (test_message) VALUES 
    ('Database setup completed successfully'),
    ('SYOS database ready for use'),
    ('Connection test data');

-- Create a simple users table for future use
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_active BOOLEAN DEFAULT TRUE
);

-- Create an index on commonly searched fields
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_users_email ON users(email);

-- Show created tables
SHOW TABLES;

-- Show the test data
SELECT * FROM connection_test;

-- Show user information
SELECT 
    User, 
    Host, 
    authentication_string 
FROM mysql.user 
WHERE User = 'syos_user';

-- Display success message
SELECT 'SYOS Database setup completed successfully!' AS status;
