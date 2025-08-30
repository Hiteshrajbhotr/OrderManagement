# Order Management System

A Spring Boot application for managing restaurants, shops, and menu items with role-based authentication and modern web UI.

## Overview

The Order Management System is a comprehensive web application built with Spring Boot that enables restaurant and shop management. It features role-based access control, shop onboarding, menu management, and modern responsive UI.

### Key Features
- **Shop Management** - Complete CRUD operations for restaurants and shops
- **Menu Management** - Menu items with categories and dietary options  
- **Role-Based Access** - Admin, Shop Owner, and Customer roles
- **Modern UI** - Responsive design with Bootstrap 5
- **RESTful API** - Clean REST endpoints for integration

### Technology Stack
- Spring Boot 3.2.0 with Java 17
- PostgreSQL database with Spring Data JPA
- Spring Security for authentication
- Thymeleaf templating with Bootstrap 5 UI
- SLF4J logging and Maven build system

## Quick Start

### Prerequisites
- Java 17+, Maven 3.6+, PostgreSQL 12+

### Setup & Run
```bash
# Start PostgreSQL (Docker recommended)
docker-compose up -d

# Build and run application
mvn clean compile
mvn spring-boot:run

# Access application at http://localhost:8080
```

### Default Test Accounts
| Role | Username | Password |
|------|----------|----------|
| Admin | `admin` | `admin123` |
| Shop Owner | `shop` | `shop123` |
| Customer | `customer` | `customer123` |

### Key URLs
- **Login**: http://localhost:8080/auth/login
- **Admin Dashboard**: http://localhost:8080/admin/dashboard  
- **Shop Management**: http://localhost:8080/shops
- **REST API**: http://localhost:8080/api/shops

The application automatically initializes with sample data including 5 demo shops with menu items for testing and development.
