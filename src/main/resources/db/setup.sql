-- PostgreSQL Database Setup Script for Student Management System

-- Create database (run this as postgres superuser)
-- CREATE DATABASE student_management;
-- CREATE DATABASE student_management_dev;

-- Create user (optional, for security)
-- CREATE USER student_app WITH PASSWORD 'your_secure_password';
-- GRANT ALL PRIVILEGES ON DATABASE student_management TO student_app;
-- GRANT ALL PRIVILEGES ON DATABASE student_management_dev TO student_app;

-- Connect to the student_management database and run the following:

-- Create schema if needed
-- CREATE SCHEMA IF NOT EXISTS public;

-- The application will automatically create tables using JPA/Hibernate
-- But you can also create them manually if needed:

/*
CREATE TABLE students (
    id BIGSERIAL PRIMARY KEY,
    first_name VARCHAR(50) NOT NULL,
    last_name VARCHAR(50) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    date_of_birth DATE NOT NULL,
    phone_number VARCHAR(20),
    course VARCHAR(100),
    enrollment_date DATE DEFAULT CURRENT_DATE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for better performance
CREATE INDEX idx_students_email ON students(email);
CREATE INDEX idx_students_course ON students(course);
CREATE INDEX idx_students_name ON students(first_name, last_name);
*/
