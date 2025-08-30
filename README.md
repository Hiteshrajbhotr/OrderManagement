# Student Management System - Spring Boot Application

A comprehensive Spring Boot application for managing student information with full CRUD operations, search functionality, and data validation.

## Project Structure

```
StudentManagement/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/studentmanagement/
│   │   │       ├── StudentManagementApplication.java     # Main Spring Boot application
│   │   │       ├── controller/                          # REST controllers
│   │   │       │   └── StudentController.java
│   │   │       ├── service/                             # Business logic
│   │   │       │   └── StudentService.java
│   │   │       ├── repository/                          # Data access layer
│   │   │       │   └── StudentRepository.java
│   │   │       ├── model/                               # Entity classes
│   │   │       │   └── Student.java
│   │   │       ├── dto/                                 # Data Transfer Objects
│   │   │       │   ├── StudentRequest.java
│   │   │       │   └── StudentResponse.java
│   │   │       └── config/                              # Configuration classes
│   │   │           └── DataInitializer.java
│   │   └── resources/
│   │       └── application.properties                   # Application configuration
│   └── test/
│       └── java/
│           └── com/example/studentmanagement/
│               ├── StudentManagementApplicationTests.java
│               └── controller/
│                   └── StudentControllerTest.java
├── pom.xml                                              # Maven dependencies
└── README.md
```

## Technologies Used

- **Spring Boot 3.2.0**
- **Java 17**
- **Maven** (Build tool)
- **PostgreSQL** (Primary database)
- **H2 Database** (In-memory database for testing)
- **Spring Data JPA** (Data persistence)
- **Spring Web** (REST API)
- **JUnit 5** (Testing)
- **HikariCP** (Connection pooling)

## Getting Started

### Prerequisites
- Java 17 or higher
- Maven 3.6 or higher
- PostgreSQL 12 or higher

### Database Setup

#### Option 1: Using Docker (Recommended)
```bash
# Start PostgreSQL using Docker Compose
docker-compose up -d

# This will create:
# - PostgreSQL database on port 5432
# - Database: student_management
# - Username: postgres
# - Password: password
```

#### Option 2: Local PostgreSQL Installation
1. **Install PostgreSQL** on your system
2. **Create databases:**
   ```sql
   CREATE DATABASE student_management;
   CREATE DATABASE student_management_dev;
   ```
3. **Update credentials** in `application.properties` if different from defaults

### Running the Application

1. **Clone and navigate to the project directory**
   ```bash
   cd EnhancedDatastructure
   ```

2. **Start PostgreSQL** (using Docker Compose or local installation)
   ```bash
   docker-compose up -d  # If using Docker
   ```

3. **Build the project**
   ```bash
   mvn clean compile
   ```

4. **Run the application**
   ```bash
   # Production mode (uses PostgreSQL)
   mvn spring-boot:run
   
   # Development mode (uses PostgreSQL with detailed logging)
   mvn spring-boot:run -Dspring-boot.run.profiles=dev
   ```

5. **Access the application**
   - API Health Check: http://localhost:8080/api/students/health
   - API Base URL: http://localhost:8080/api/students

### Running Tests

```bash
mvn test
```

## Features

- **Complete CRUD Operations** - Create, Read, Update, Delete students
- **Advanced Search** - Search by name, course, email
- **Data Validation** - Comprehensive input validation with error messages
- **Sample Data** - Automatic initialization with sample student data
- **RESTful API** - Clean REST endpoints with proper HTTP status codes
- **Unit Testing** - Comprehensive test coverage
- **H2 Database** - In-memory database with web console for development

## API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| `GET` | `/api/students/health` | Health check endpoint |
| `POST` | `/api/students` | Create new student |
| `GET` | `/api/students` | Get all students |
| `GET` | `/api/students/{id}` | Get student by ID |
| `PUT` | `/api/students/{id}` | Update student |
| `DELETE` | `/api/students/{id}` | Delete student |
| `GET` | `/api/students/search?name={name}` | Search students by name |
| `GET` | `/api/students/course/{course}` | Get students by course |
| `GET` | `/api/students/email/{email}` | Get student by email |

## Sample API Usage

### Create a Student
```bash
curl -X POST http://localhost:8080/api/students \
  -H "Content-Type: application/json" \
  -d '{
    "firstName": "John",
    "lastName": "Doe",
    "email": "john.doe@example.com",
    "dateOfBirth": "2000-01-15",
    "phoneNumber": "1234567890",
    "course": "Computer Science"
  }'
```

### Get All Students
```bash
curl http://localhost:8080/api/students
```

### Search Students by Name
```bash
curl "http://localhost:8080/api/students/search?name=John"
```

## Configuration

The application uses H2 in-memory database for development. Configuration can be modified in `src/main/resources/application.properties`.

## Development

This Student Management System provides a complete foundation for managing student information with modern Spring Boot practices and comprehensive API functionality.
