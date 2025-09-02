# 🐾 Virtual Pets Backend API

A Spring Boot API for managing virtual pets and users, with role-based access (`ROLE_ADMIN`, `ROLE_USER`), JWT authentication, and Swagger/OpenAPI documentation. Includes endpoints for user registration/login and pet management.

---

## 📌 Requirements

To run this project, you will need:

- **Java 21** (JDK installed)  
- **Maven** (`./mvnw` wrapper included)  
- **Docker** (for MySQL container)  
- Internet connection to download dependencies  

Optional but recommended for testing and Swagger UI:

- Browser to access Swagger/OpenAPI docs
- Postman or similar API client

---

## 📦 Tech Stack

- **Java 21**  
- **Spring Boot 3.2.4**  
- **Spring Data JPA**  
- **MySQL 8** (Dockerized)  
- **Spring Security** + JWT Authentication  
- **Lombok**  
- **Swagger/OpenAPI** (`springdoc-openapi-starter-webmvc-ui`)  
- **JUnit 5** + **Mockito** for tests  

---

## 📁 Project Structure

```
virtual-pets-backend/
├── src/
│   ├── main/
│   │   ├── java/com/virtualpets/backend/
│   │   │   ├── controller/        # REST endpoints
│   │   │   ├── dto/               # Request & Response DTOs
│   │   │   ├── exception/         # Custom exceptions
│   │   │   ├── model/             # Entities: User, Role, Pet
│   │   │   ├── repository/        # JPA Repositories
│   │   │   ├── service/           # Services and implementations
│   │   │   ├── util/              # JWT & helpers
│   │   │   └── VirtualPetsBackendApplication.java
│   └── test/
│       └── java/com/virtualpets/virtual_pets_backend/
│           ├── AuthControllerTest.java
│           ├── AuthServiceImplTest.java
│           ├── JwtUtilTest.java
│           ├── PetControllerIntegrationTest.java
│           ├── PetControllerUnitTest.java
│           ├── PetRepositoryTest.java
│           └── PetServiceImplTest.java
├── docker-compose.yml              # MySQL container
├── application.properties
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

### 2️⃣ Start MySQL with Docker Compose

A `docker-compose.yml` is included:

```yaml
version: "3.8"
services:
  mysql:
    image: mysql:8.0
    container_name: virtualpets-mysql
    restart: always
    environment:
      MYSQL_ROOT_PASSWORD: root
      MYSQL_DATABASE: virtualpets
      MYSQL_USER: appuser
      MYSQL_PASSWORD: apppassword
    ports:
      - "3307:3306"
    volumes:
      - mysql_data:/var/lib/mysql

volumes:
  mysql_data:
```

Run:

```bash
docker-compose up -d
```

> MySQL will be available on port `3307` (mapped to container’s 3306).

### 3️⃣ Configure Spring Boot

Update `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3307/virtualpets
spring.datasource.username=appuser
spring.datasource.password=apppassword

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect
```

> `ddl-auto=update` will create/update tables automatically.

### 4️⃣ Run the Application

```bash
./mvnw spring-boot:run
```

- Default URL: [http://localhost:8080](http://localhost:8080)  
- Admin credentials auto-created:
  - **Username:** admin  
  - **Password:** admin123  

---

## 🔌 API Endpoints

### Authentication

| Method | Endpoint        | Description             |
|--------|----------------|-------------------------|
| POST   | `/auth/register` | Register new user       |
| POST   | `/auth/login`    | Login & get JWT token   |

### Users

| Method | Endpoint             | Description                  |
|--------|--------------------|------------------------------|
| GET    | `/users`            | List all users (admin only)  |
| GET    | `/users/{username}` | Get user info (self or admin)|

### Pets

| Method | Endpoint           | Description                         |
|--------|------------------|-------------------------------------|
| POST   | `/pets`            | Create a new pet                     |
| GET    | `/pets`            | List pets (user sees own, admin sees all) |
| GET    | `/pets/{id}`       | Get pet details (owner/admin)        |
| PUT    | `/pets/{id}`       | Update pet (owner/admin)             |
| DELETE | `/pets/{id}`       | Delete pet (owner/admin)             |

---

## 🔐 Roles & Authorization

| Role         | Permissions                                   |
|--------------|-----------------------------------------------|
| `ROLE_USER`  | Access only **their own** pets               |
| `ROLE_ADMIN` | Full access to **all** pets                  |

> JWT token required for all protected endpoints:  
> `Authorization: Bearer <JWT_TOKEN>`

---

## 🧪 Testing

The project includes **unit and integration tests**:

### Unit Tests
- `AuthControllerTest` – tests user registration and login endpoints.
- `AuthServiceImplTest` – tests authentication logic, password encoding, and error cases.
- `JwtUtilTest` – tests token generation, validation, username/role extraction, and error handling.
- `PetControllerUnitTest` – verifies pet endpoint logic in isolation.
- `PetServiceImplTest` – tests pet service CRUD operations and role-based access.
- `PetRepositoryTest` – tests repository methods like save, findByOwner, delete.

### Integration Tests
- `PetControllerIntegrationTest` – full API flow including CRUD operations, owner/admin access, and JWT auth.

🧪 Run tests:

```bash
./mvnw test
```

---

## 📚 Swagger/OpenAPI

- **Swagger UI:** [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)  
- **OpenAPI JSON:** [http://localhost:8080/v3/api-docs](http://localhost:8080/v3/api-docs)

---

## 🐳 Docker Deployment (Optional)

1. Build image:

```bash
docker build -t virtual-pet-app .
```

2. Run container:

```bash
docker run -p 8080:8080 virtual-pet-app
```

3. Access app:

- [http://localhost:8080](http://localhost:8080)  
- Swagger docs: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## 👨‍💻 Author

**Adrià Vargas**  
🔗 [GitHub](https://github.com/AdriaVH)

