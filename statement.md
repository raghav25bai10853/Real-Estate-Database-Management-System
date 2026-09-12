# Project Statement: Real Estate Management System

## Problem Statement
Buying, selling, and renting properties is traditionally managed through fragmented channels — brokers, newspaper listings, or word of mouth — with no centralized, structured way to store property details or track customer interest. Real estate agencies and small property dealers need a simple, reliable backend system to store property listings, keep them searchable, and capture inquiries from interested customers without depending on manual paperwork or spreadsheets. This project addresses that gap by providing a structured, API-driven backend that any client application (web, mobile, or admin dashboard) can plug into to manage properties and customer inquiries.

## Scope
This project is a **backend-only REST API** and does not include:
- A frontend/UI (the API is meant to be consumed by Postman, curl, or a separate client app)
- Payment processing or transaction handling
- User registration/authentication beyond a single admin login
- Image/file uploads for property photos
- AI/ML-based recommendations

What **is** in scope:
- Full CRUD operations for property listings
- Search/filtering of properties by city, type, price range, bedrooms, and status
- A single admin login for restricted operations
- An inquiry system so customers can express interest in a specific property
- Persistent storage of all data using a file-based H2 database, so records survive application restarts

## Target Users
- **Property dealers / small real estate agencies** who need a simple system to list and manage available properties.
- **Admin/staff** who log in to add, update, or remove property listings and review customer inquiries.
- **Prospective buyers/tenants** (as API consumers via a future frontend) who search available properties and submit inquiries.
- **Developers/students** who want a clean, beginner-friendly reference implementation of a layered Spring Boot REST API using plain JDBC (no JPA/ORM).

## High-Level Features
1. **Property Management (CRUD)** — Add, view, update, and delete property listings, each with details like title, description, city, address, type, price, bedrooms, bathrooms, area, and status.
2. **Property Search** — Filter listings by any combination of city, property type, minimum/maximum price, number of bedrooms, and availability status.
3. **Admin Authentication** — A basic login endpoint that validates admin credentials against the database before allowing management actions.
4. **Inquiry Management** — Customers can submit an inquiry (name, email, phone, message) tied to a specific property; admins can view all inquiries or inquiries filtered by property.
5. **Persistent, File-Based Storage** — Uses an embedded H2 database in file mode so that all property and inquiry data is retained across application restarts, without needing an external database server.
6. **Clean, Layered Architecture** — Controller → Service → DAO → Model separation using Spring's `JdbcTemplate`, making the codebase easy to read, test, and extend.
