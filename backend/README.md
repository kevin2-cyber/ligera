# Ligera Backend

Backend REST API service for the Ligera Clothing Marketplace application.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.1-green)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-13.0+-blue)
![JWT](https://img.shields.io/badge/JWT-Auth-yellow)

## Project Overview

Ligera Backend is a RESTful API service that powers the Ligera Clothing Marketplace mobile application. It provides authentication, user management, and other core functionality required by the mobile client.

### Technology Stack

- **Java 17**: Core programming language
- **Spring Boot 3.2.1**: Application framework
- **Spring Security**: Authentication and authorization
- **Spring Data JPA**: Data access layer
- **PostgreSQL**: Primary database
- **JWT**: Token-based authentication
- **Maven**: Build and dependency management
- **JUnit 5 & Spring Test**: Testing framework
- **Swagger/OpenAPI**: API documentation

### Features

- User registration and authentication with JWT
- Role-based access control
- User profile management
- Secure password handling
- Comprehensive error handling
- API documentation with Swagger/OpenAPI
- Integration testing

## Setup Instructions

### Prerequisites

- Java 17 or higher
- Maven 3.6.3 or higher
- PostgreSQL 13.0 or higher
- IDE of your choice (IntelliJ IDEA, Eclipse, VS Code)

### Database Setup

1. Install PostgreSQL on your machine
2. Create a new database:

```sql
CREATE DATABASE ligera_db;
CREATE USER postgres WITH ENCRYPTED PASSWORD 'postgres';
GRANT ALL PRIVILEGES ON DATABASE ligera_db TO postgres;
```

3. The application will automatically create the necessary tables on startup (using `spring.jpa.hibernate.ddl-auto=update`)

### Environment Configuration

Create or modify `application.properties` in `src/main/resources` with your specific configuration:

```properties
# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/ligera_db
spring.datasource.username=postgres
spring.datasource.password=postgres

# JWT Configuration
app.jwt.secret=your_jwt_secret_key_should_be_very_long_and_secure
app.jwt.expiration-ms=86400000
```

### Build and Run

1. Clone the repository:
```bash
git clone https://github.com/kevin2-cyber/ligera.git
cd ligera/backend/ligera-backend
```

2. Build the application:
```bash
mvn clean install
```

3. Run the application:
```bash
mvn spring-boot:run
```

The application will start on port 8080 by default. You can access it at `http://localhost:8080/api`

## API Documentation

### Authentication Endpoints

| Method | Endpoint                    | Description                | Request Body                              | Response                         |
|--------|-----------------------------|-----------------------------|-------------------------------------------|----------------------------------|
| POST   | `/api/v1/auth/register`     | Register a new user         | `name`, `email`, `password`               | JWT token and user details       |
| POST   | `/api/v1/auth/login`        | Authenticate a user         | `email`, `password`                       | JWT token and user details       |
| POST   | `/api/v1/auth/change-password` | Change password          | `currentPassword`, `newPassword`          | Success message                  |

### User Management Endpoints

| Method | Endpoint                    | Description                | Authentication Required | Response                         |
|--------|-----------------------------|-----------------------------|-------------------------|----------------------------------|
| GET    | `/api/v1/users/me`          | Get current user profile    | Yes                     | User details                     |
| PUT    | `/api/v1/users/me`          | Update user profile         | Yes                     | Updated user details             |

### Swagger/OpenAPI Documentation

Swagger UI is available at: `http://localhost:8080/api/swagger-ui.html`

API Documentation: `http://localhost:8080/api/v3/api-docs`

## Development

### Development Environment Setup

1. Fork and clone the repository
2. Set up your IDE with Java 17
3. Install Maven
4. Set up PostgreSQL locally
5. Configure application properties for your local environment

### Testing

The project includes unit and integration tests. To run all tests:

```bash
mvn test
```

To run only unit tests:

```bash
mvn test -Dtest=*Test
```

To run only integration tests:

```bash
mvn test -Dtest=*IntegrationTest
```

### Code Structure

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── ligera/
│   │           └── backend/
│   │               ├── config/           # Configuration classes
│   │               ├── controller/       # REST API controllers
│   │               ├── dtos/             # Data Transfer Objects
│   │               │   ├── request/      # Request DTOs
│   │               │   └── response/     # Response DTOs
│   │               ├── enums/            # Enumerations
│   │               ├── exception/        # Exception handling
│   │               ├── models/           # Entity models
│   │               ├── repositories/     # Data repositories
│   │               ├── security/         # Security configuration
│   │               │   └── filter/       # Security filters
│   │               └── service/          # Business logic
│   └── resources/
│       └── application.properties        # Application configuration
├── test/
│   ├── java/
│   │   └── com/
│   │       └── ligera/
│   │           └── backend/
│   │               ├── integration/      # Integration tests
│   │               └── unit/             # Unit tests
│   └── resources/
│       └── application-test.properties   # Test configuration
```

## Security

### JWT Configuration

JWT tokens are used for authentication and authorization. The tokens have the following properties:

- Token expiration: 24 hours by default (configurable)
- Token signing: HMAC-SHA256 algorithm
- Token format: Bearer token in Authorization header

### Password Policies

- Passwords must be at least 6 characters long
- Passwords are stored using BCrypt encryption
- Password validation is performed on both client and server sides

### CORS Settings

The API supports Cross-Origin Resource Sharing (CORS) with the following configuration:

- Allowed origins: Configurable, set to `*` by default (should be restricted in production)
- Allowed methods: GET, POST, PUT, DELETE, OPTIONS
- Allowed headers: Authorization, Content-Type, X-Requested-With
- Max age: 3600 seconds (1 hour)

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Contact

For any questions or concerns, please contact the project maintainers at dev@ligera.com.

