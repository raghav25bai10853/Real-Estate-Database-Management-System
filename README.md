# Real Estate Management System (Backend Only)

A beginner-friendly, backend-only REST API built with **Spring Boot + JdbcTemplate + H2 (file-based)**.
No frontend, no JPA/Hibernate, no Spring Security/JWT, no Docker, no AI/ML — just plain layered Java.

## Tech Stack
- Java 25 LTS
- Spring Boot 3.3.4 (Web + JDBC starters)
- H2 Database — **file-based**, so your data survives app restarts
- Maven

## Project Layers
```
controller/   -> REST endpoints (HTTP in/out)
service/      -> business logic & validation
dao/          -> JdbcTemplate + raw SQL (only place that touches the DB)
model/        -> plain Java objects (POJOs)
dto/          -> request/response wrapper objects
exception/    -> custom exceptions + global error handler
```

## Folder Structure
```
real-estate-management-system/
├── pom.xml
├── src/main/java/com/realestate/
│   ├── RealEstateApplication.java
│   ├── controller/  (PropertyController, AdminController, InquiryController)
│   ├── service/     (interfaces + impl/)
│   ├── dao/          (interfaces + Impl classes using JdbcTemplate)
│   ├── model/        (Property, Admin, Inquiry)
│   ├── dto/          (ApiResponse, LoginRequest)
│   └── exception/    (ResourceNotFoundException, GlobalExceptionHandler)
└── src/main/resources/
    ├── application.properties
    ├── schema.sql
    └── data.sql
```

## How to Run

### 1. Prerequisites
- JDK 25+
- Maven 3.6+ (or use your IDE's built-in Maven)

### 2. Run it
```bash
cd real-estate-management-system
mvn spring-boot:run
```
Or build a runnable jar:
```bash
mvn clean package
java -jar target/real-estate-management-system.jar
```

The app starts on **http://localhost:8080**.

### 3. Where is my data stored?
This project uses **file-based H2** (not in-memory), configured in `application.properties`:
```properties
spring.datasource.url=jdbc:h2:file:./data/realestatedb;AUTO_SERVER=TRUE;DB_CLOSE_ON_EXIT=FALSE
```
This creates a `data/realestatedb.mv.db` file next to your project. Stop the app, start it again — your data is still there. `schema.sql` and `data.sql` are written to be safe to re-run (they won't duplicate or wipe rows).

### 4. View the database in browser (optional)
Visit: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:file:./data/realestatedb`
- Username: `sa`
- Password: *(leave blank)*

### 5. Default admin login
```
username: admin
password: admin123
```

---

## API Reference

All responses follow this shape:
```json
{ "success": true, "message": "...", "data": { ... } }
```

### Property CRUD

**Create a property**
```bash
curl -X POST http://localhost:8080/api/properties \
  -H "Content-Type: application/json" \
  -d '{
        "title": "Modern 3BHK Flat",
        "description": "Near IT park",
        "city": "Bhopal",
        "address": "Arera Colony, Bhopal",
        "type": "APARTMENT",
        "price": 6500000,
        "bedrooms": 3,
        "bathrooms": 2,
        "areaSqft": 1450,
        "status": "AVAILABLE"
      }'
```

**Get all properties**
```bash
curl http://localhost:8080/api/properties
```

**Get one property by id**
```bash
curl http://localhost:8080/api/properties/1
```

**Update a property**
```bash
curl -X PUT http://localhost:8080/api/properties/1 \
  -H "Content-Type: application/json" \
  -d '{
        "title": "Modern 3BHK Flat (Price Reduced)",
        "description": "Near IT park",
        "city": "Bhopal",
        "address": "Arera Colony, Bhopal",
        "type": "APARTMENT",
        "price": 6200000,
        "bedrooms": 3,
        "bathrooms": 2,
        "areaSqft": 1450,
        "status": "AVAILABLE"
      }'
```

**Delete a property**
```bash
curl -X DELETE http://localhost:8080/api/properties/1
```

### Search Properties
Query params: `city`, `type`, `minPrice`, `maxPrice`, `bedrooms`, `status` — **all optional**, combine any of them.

```bash
# By city
curl "http://localhost:8080/api/properties/search?city=Bhopal"

# By type + status
curl "http://localhost:8080/api/properties/search?type=APARTMENT&status=AVAILABLE"

# By price range
curl "http://localhost:8080/api/properties/search?minPrice=3000000&maxPrice=9000000"

# By bedrooms
curl "http://localhost:8080/api/properties/search?bedrooms=3"

# Combine everything
curl "http://localhost:8080/api/properties/search?city=Bhopal&type=VILLA&minPrice=5000000&maxPrice=15000000&bedrooms=4&status=AVAILABLE"
```

### Admin Login
```bash
curl -X POST http://localhost:8080/api/admin/login \
  -H "Content-Type: application/json" \
  -d '{ "username": "admin", "password": "admin123" }'
```

### Inquiries

**Submit an inquiry about a property**
```bash
curl -X POST http://localhost:8080/api/inquiries \
  -H "Content-Type: application/json" \
  -d '{
        "propertyId": 1,
        "name": "Priya Verma",
        "email": "priya.verma@example.com",
        "phone": "9123456780",
        "message": "Is this property still available? I would like to schedule a visit."
      }'
```

**Get all inquiries**
```bash
curl http://localhost:8080/api/inquiries
```

**Get one inquiry**
```bash
curl http://localhost:8080/api/inquiries/1
```

**Get all inquiries for a specific property**
```bash
curl http://localhost:8080/api/inquiries/property/1
```

**Delete an inquiry**
```bash
curl -X DELETE http://localhost:8080/api/inquiries/1
```

---

## Notes for Beginners
- **No JPA**: All SQL is written explicitly inside the `dao/` classes using `JdbcTemplate`. This is intentional so you can see exactly what SQL runs.
- **No JWT/Spring Security**: Admin login just checks the DB and returns success/failure as JSON. Good enough for learning; not secure for production.
- **Passwords are stored in plain text** in `data.sql`/DB for simplicity. Never do this in a real production app — use BCrypt hashing instead.
- **Validation**: Property/Inquiry/LoginRequest use `@Valid` + Jakarta Bean Validation annotations (`@NotBlank`, `@Email`, etc.). Sending bad data returns a clear `400` JSON error instead of a crash.
- **Error handling**: `GlobalExceptionHandler` catches errors app-wide so you always get clean JSON responses like:
  ```json
  { "success": false, "message": "Property not found with id: 99", "data": null }
  ```
