-- Migration to add address fields and remove company field
-- V3__Add_Address_Fields_Remove_Company.sql

-- Add new address columns to customers table
ALTER TABLE customers ADD COLUMN city VARCHAR(100);
ALTER TABLE customers ADD COLUMN state VARCHAR(100);
ALTER TABLE customers ADD COLUMN country VARCHAR(100);
ALTER TABLE customers ADD COLUMN pincode VARCHAR(20);

-- Migrate existing company data to city field (temporary solution)
UPDATE customers SET city = company WHERE company IS NOT NULL;

-- Drop the company column
ALTER TABLE customers DROP COLUMN company;

-- Add indexes for better query performance on address fields
CREATE INDEX idx_customers_city ON customers(city);
CREATE INDEX idx_customers_state ON customers(state);
CREATE INDEX idx_customers_country ON customers(country);
CREATE INDEX idx_customers_pincode ON customers(pincode);
