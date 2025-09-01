# 🐾 Virtual Pets Backend API

A Spring Boot API for managing virtual pets and users, with role-based access (`ROLE_ADMIN`, `ROLE_USER`), JWT authentication, and Swagger/OpenAPI documentation. Includes endpoints for user registration/login and pet management.

---

## 📦 Tech Stack

- **Java 21**
- **Spring Boot 3.2.4**
- **Spring Data JPA**
- **MySQL 8**
- **Spring Security**
- **JWT Authentication**
- **Lombok**
- **Swagger/OpenAPI** (`springdoc-openapi-starter-webmvc-ui`)
- **JUnit 5**, **Mockito** for testing

---

## 📁 Project Structure

```
virtual-pets-backend/
├── src/
│   ├── main/
│   │   ├── java/com/virtualpets/backend/
│   │   │   ├── dto/
│   │   │   │   ├── request/      # Request DTOs: LoginRequest, RegisterRequest, PetRequest
│   │   │   │   └── response/     # Response DTOs: AuthResponse, UserResponse, PetResponse
│   │   │   ├── exception/        # Custom exceptions and GlobalExceptionHandler
│   │   │   ├── model/            # Entities: User, Role, Pet
│   │   │   ├── repository/       # JPA Repositories: UserRepository, RoleRepository, PetRepository
│   │   │   ├── service/          # Service interfaces: AuthService, UserService, PetService
│   │   │   ├── service/impl/     # Service implementations
│   │   │   ├── util/             # Utilities: JwtUtil, DataInitializer
│   │   │   └── VirtualPetsBackendApplication.java  # Main Spring Boot entry point
│   └── resources/
│       └── application.properties
├── pom.xml
└── README.md
```

---

## 🚀 Getting Started

### 1️⃣ Clone the repo

```bash
git clone https://github.com/AdriaVH/5.2-PetApp-N1.git
cd virtual-pets-backend
```

### 2️⃣ Configure MySQL

Create the database and user:

```sql
CREATE DATABASE virtualpets;
CREATE USER 'appuser'@'localhost' IDENTIFIED BY 'apppassword';
GRANT ALL PRIVILEGES ON virtualpets.* TO 'appuser'@'localhost';
FLUSH PRIVILEGES;
```

Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/virtualpets
spring.datasource.username=appuser
spring.datasource.password=apppassword

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

> `ddl-auto=update` will create/update tables automatically. Ensure MySQL is running before starting the app.

### 3️⃣ Run the Application

```bash
./mvnw spring-boot:run
```

- Default URL: `http://localhost:8080`
- Admin credentials auto-created:
  - Username: `admin`
  - Password: `admin123`

---

## 🔌 API Endpoints

### 4.1 Authentication

| Method | Endpoint        | Description               |
|--------|----------------|---------------------------|
| POST   | `/auth/register` | Register a new user      |
| POST   | `/auth/login`    | Login and get JWT token  |

**Request/Response DTOs:**

- `RegisterRequest`: `username`, `password`  
- `LoginRequest`: `username`, `password`  
- `AuthResponse`: `token`, `username`  

---

### 4.2 Users

| Method | Endpoint             | Description                         |
|--------|--------------------|-------------------------------------|
| GET    | `/users`            | List all users (admin only)         |
| GET    | `/users/{username}` | Get user info (self or admin)       |

Response DTO: `UserResponse` with `id`, `username`, `roles`.

---

### 4.3 Pets

| Method | Endpoint           | Description                                    |
|--------|------------------|------------------------------------------------|
| POST   | `/pets`            | Create a new pet for the authenticated user   |
| GET    | `/pets`            | List all pets (admin sees all)                |
| GET    | `/pets/{id}`       | Get pet details (owner or admin)              |
| PUT    | `/pets/{id}`       | Update pet (owner or admin)                   |
| DELETE | `/pets/{id}`       | Delete pet (owner or admin)                   |

Request/Response DTO: `PetRequest` (`name`, `type`, `age`) and `PetResponse` (`id`, `name`, `type`, `age`, `ownerUsername`).  

---

## 🔐 JWT Authentication

All protected endpoints require a Bearer token in the `Authorization` header:

```
Authorization: Bearer <JWT_TOKEN>
```

- Token contains `username` and `roles`.  
- Token expiration: 1 hour.

---

## 📚 Swagger/OpenAPI Documentation

- Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)  
- OpenAPI JSON: [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 🧪 Running Tests

```bash
./mvnw test
```

- Unit tests with **JUnit 5** and **Mockito**.

---

## 👨‍💻 Author

**Your Name**  
🔗 [GitHub](https://github.com/AdriaVH)

