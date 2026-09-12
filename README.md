# Real Estate Management System

## Overview
This is a **backend-only REST API** for managing real estate property listings, built with **Spring Boot + JdbcTemplate + H2 (file-based database)**. It allows an admin to manage property records (add/update/delete/search) and allows customers to submit inquiries about a property. There is no frontend — it is meant to be consumed via Postman, curl, or any client application. The project follows a clean layered architecture (Controller → Service → DAO → Model) using plain JDBC (no JPA/Hibernate), so every SQL query is explicit and easy to follow.

## Features
- **Property CRUD** — create, read, update, delete property listings
- **Search** — filter properties by city, type, price range, bedrooms, and status
- **Admin login** — simple username/password check against the database
- **Inquiry module** — customers can submit inquiries for a specific property; admin can view all inquiries or inquiries per property
- **Persistent storage** — H2 is configured in file mode, so data survives an application restart
- **Consistent JSON responses** — every endpoint returns `{ success, message, data }`
- **Input validation** — invalid requests return clear `400` errors instead of crashing
- **Global exception handling** — clean JSON errors for not-found (`404`) and server errors (`500`)

## Technologies / Tools Used
| Category         | Tech |
|-------------------|------|
| Language           | Java 17 |
| Framework          | Spring Boot 3.3.4 |
| Data access        | Spring JDBC (`JdbcTemplate`) — no JPA/Hibernate |
| Database           | H2 (file-based, embedded) |
| Build tool         | Maven |
| Validation         | Jakarta Bean Validation |
| API testing        | Postman / curl |

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

## Steps to Install & Run

### 1. Prerequisites
- JDK 17+
- Maven 3.6+ (or use your IDE's built-in Maven)

### 2. Clone and run
```bash
git clone https://github.com/raghav25bai10853/Real-Estate-Database-Management-System.git
cd Real-Estate-Database-Management-System
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

## Instructions for Testing
You can test all endpoints in three ways:
1. **Postman** — import `postman_collection.json` (included in this repo) into Postman. It has a `baseUrl` variable already set to `http://localhost:8080`, and one ready-made request per endpoint.
2. **curl** — copy-paste any command from the [API Reference](#api-reference) section below into a terminal while the app is running.
3. **H2 Console** — visit `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:file:./data/realestatedb`, username `sa`, no password) to inspect the actual database tables and rows after running requests.

**Suggested test flow:**
1. Start the app (`mvn spring-boot:run`).
2. `GET /api/properties` → should return the 6 sample properties already seeded from `data.sql`.
3. `POST /api/admin/login` with `admin` / `admin123` → should return success.
4. `POST /api/properties` with a new property → should return `201 Created` with the new property (including a generated `id`).
5. `GET /api/properties/search?city=Bhopal` → should return only Bhopal properties.
6. `POST /api/inquiries` with a valid `propertyId` → should succeed. Try an invalid `propertyId` (e.g. `9999`) → should return a clean `404` error.
7. Restart the app and repeat step 2 → the same data (plus anything you added) should still be there, proving persistence.

## Screenshots
_(Optional — add screenshots here of Postman requests/responses, the H2 console, or the terminal showing the app running, e.g.:)_
```
![Get all properties in Postman](screenshots/get-properties.png)
![App running in terminal](screenshots/app-startup.png)
```

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


