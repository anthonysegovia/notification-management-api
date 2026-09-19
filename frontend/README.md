# Notification Management System

A full-stack notification management application built with **Java, Spring Boot, React, PostgreSQL, JWT, and Docker**.

The application allows authenticated users to create, view, update, and delete their own notifications. Notifications can be sent through multiple channels using an extensible **Strategy Pattern** implementation.

Currently supported channels:

- Email
- SMS
- Push

The project also includes a responsive React interface with **Light and Dark themes**, API documentation with Swagger/OpenAPI, unit tests, and a complete Docker Compose environment.

---

## Features

### Authentication

- User registration with email and password
- Password hashing using BCrypt
- JWT-based authentication
- Stateless Spring Security configuration
- Protected API endpoints
- Automatic logout when the JWT expires

### Notification Management

Authenticated users can:

- Create notifications
- View their notifications
- View a specific notification
- Edit notifications
- Delete notifications

Notifications are always scoped to the authenticated user.

### Notification Channels

The notification system uses the **Strategy Pattern** to keep channel-specific behavior independent from the notification business logic.

#### Email

- Validates recipient email format
- Generates an email template
- Simulates delivery through application logs

#### SMS

- Validates the recipient phone number
- Limits messages to 160 characters
- Logs recipient, timestamp, and message

#### Push

- Validates the device token
- Generates a push payload
- Logs delivery status

The architecture allows additional channels to be introduced without modifying the existing notification service.

### Frontend

The React application includes:

- Registration
- Login
- JWT session persistence
- Notification dashboard
- Create notification form
- Edit notification form
- Delete functionality
- Dynamic Email / SMS / Push forms
- SMS character counter
- Responsive layout
- Light / Dark theme
- Theme persistence with localStorage
- Automatic logout for expired sessions

---

## Tech Stack

### Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Spring Security
- Hibernate
- Jakarta Validation
- JJWT
- BCrypt
- Maven

### Frontend

- React
- Vite
- JavaScript
- Fetch API
- CSS
- ESLint

### Database

- PostgreSQL 17

### Documentation & Testing

- Swagger / OpenAPI
- JUnit
- Mockito

### Infrastructure

- Docker
- Docker Compose
- Nginx

---

## Architecture

```text
                         Browser
                            |
                            |
                  http://localhost:5173
                            |
                            v
                  +-------------------+
                  |   React + Vite    |
                  |       Nginx       |
                  +---------+---------+
                            |
                            | REST / JSON
                            | JWT Bearer Token
                            |
                            v
                  +-------------------+
                  |    Spring Boot    |
                  |      REST API     |
                  +---------+---------+
                            |
              +-------------+-------------+
              |                           |
              v                           v
      +---------------+          +------------------+
      | PostgreSQL    |          | Notification     |
      | Database      |          | Sender Factory   |
      +---------------+          +--------+---------+
                                          |
                          +---------------+---------------+
                          |               |               |
                          v               v               v
                       Email             SMS             Push
                      Strategy         Strategy         Strategy
```

---

## Notification Strategy Pattern

Each notification channel implements the same interface:

```java
public interface NotificationSender {

    NotificationChannel getChannel();

    void send(Notification notification);
}
```

The application contains independent implementations for:

```text
NotificationSender
       |
       +-- EmailNotificationSender
       |
       +-- SmsNotificationSender
       |
       +-- PushNotificationSender
```

`NotificationSenderFactory` discovers the available implementations and maps them by notification channel.

The notification service therefore does not need channel-specific conditional logic such as:

```java
if (channel == EMAIL) {
    ...
} else if (channel == SMS) {
    ...
}
```

Instead:

```text
NotificationService
        |
        v
NotificationSenderFactory
        |
        v
NotificationSender
```

This follows the **Open/Closed Principle**: new notification channels can be added by implementing another strategy without modifying the existing notification service.

---

## Data Model

### User

```text
User
----------------
id
email
password
```

### Notification

```text
Notification
----------------
id
title
content
channel
recipient
createdAt
user_id
```

Relationship:

```text
User 1 -------- N Notification
```

A user can own multiple notifications, while each notification belongs to exactly one user.

---

## Authentication Flow

```text
Register
   |
   v
Password
   |
   v
BCrypt Hash
   |
   v
PostgreSQL
```

Login:

```text
Email + Password
       |
       v
Verify BCrypt Hash
       |
       v
Generate JWT
       |
       v
Return Access Token
```

Authenticated requests:

```text
Authorization: Bearer <JWT>
             |
             v
JWT Authentication Filter
             |
             v
Validate Signature / Expiration
             |
             v
Verify User
             |
             v
Spring Security Context
             |
             v
Protected Endpoint
```

The API is stateless and does not maintain server-side HTTP sessions.

---

## Authorization

Notifications are queried using both the notification identifier and the authenticated user's email.

For example:

```java
findByIdAndUserEmail(id, email)
```

This prevents one authenticated user from accessing another user's notification simply by changing the notification ID in the URL.

---

# Running the Application

## Option 1 — Docker Compose

This is the recommended way to run the project.

### Requirements

Install:

- Docker
- Docker Compose

Then, from the project root:

```bash
docker compose up --build
```

Docker Compose starts:

```text
notifications-db
notifications-api
notifications-frontend
```

Once all services are running:

### Frontend

```text
http://localhost:5173
```

### Backend API

```text
http://localhost:8080
```

### Swagger UI

```text
http://localhost:8080/swagger-ui/index.html
```

### PostgreSQL

```text
Host: localhost
Port: 5433
Database: notifications
Username: postgres
Password: postgres
```

The PostgreSQL container internally uses port `5432`, while the host exposes it through `5433` to avoid conflicts with local PostgreSQL installations.

### Stop the application

```bash
docker compose down
```

To also delete the PostgreSQL volume:

```bash
docker compose down -v
```

---

# Running Locally Without Docker

## Backend

### Requirements

- Java 21
- PostgreSQL
- Maven or the included Maven Wrapper

The default local database configuration expects PostgreSQL at:

```text
jdbc:postgresql://localhost:5433/notifications
```

Run the backend on Windows:

```powershell
.\mvnw.cmd spring-boot:run
```

On Linux/macOS:

```bash
./mvnw spring-boot:run
```

The API starts at:

```text
http://localhost:8080
```

---

## Frontend

Requirements:

- Node.js
- npm

From the project root:

```bash
cd frontend
npm install
npm run dev
```

The Vite development server starts at:

```text
http://localhost:5173
```

The frontend uses the following API URL by default:

```text
http://localhost:8080
```

It can be overridden using:

```text
VITE_API_URL
```

---

# Environment Variables

The backend supports the following environment variables:

| Variable | Description | Docker value |
|---|---|---|
| `DB_URL` | PostgreSQL JDBC URL | `jdbc:postgresql://postgres:5432/notifications` |
| `DB_USERNAME` | Database username | `postgres` |
| `DB_PASSWORD` | Database password | `postgres` |
| `JWT_SECRET` | Secret used to sign JWT tokens | Development secret |
| `JWT_EXPIRATION` | JWT expiration time in milliseconds | `3600000` |

Frontend:

| Variable | Description | Default |
|---|---|---|
| `VITE_API_URL` | Backend API URL | `http://localhost:8080` |

> The credentials and JWT secret included in the Docker configuration are intended only for local development and demonstration purposes. Production environments should provide secrets through secure environment configuration.

---

# REST API

## Authentication

### Register

```http
POST /api/auth/register
```

Example:

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

### Login

```http
POST /api/auth/login
```

Example:

```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

Response:

```json
{
  "accessToken": "<JWT>"
}
```

---

## Notifications

All notification endpoints require:

```http
Authorization: Bearer <JWT>
```

### Create Notification

```http
POST /api/notifications
```

Example:

```json
{
  "title": "Welcome",
  "content": "Welcome to Notification Manager",
  "channel": "EMAIL",
  "recipient": "user@example.com"
}
```

Available channels:

```text
EMAIL
SMS
PUSH
```

### List Notifications

```http
GET /api/notifications
```

Only notifications belonging to the authenticated user are returned.

### Get Notification

```http
GET /api/notifications/{id}
```

### Update Notification

```http
PUT /api/notifications/{id}
```

Example:

```json
{
  "title": "Updated notification",
  "content": "Updated content",
  "channel": "SMS",
  "recipient": "+528112345678"
}
```

### Delete Notification

```http
DELETE /api/notifications/{id}
```

---

# Error Handling

The API uses centralized exception handling and returns structured JSON errors.

Example:

```json
{
  "timestamp": "2026-09-19T12:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Notification not found",
  "path": "/api/notifications/99"
}
```

Handled scenarios include:

- Invalid credentials
- Duplicate email registration
- Resource not found
- Invalid notification data
- Bean validation errors
- Unauthorized requests

---

# Swagger / OpenAPI

Interactive API documentation is available at:

```text
http://localhost:8080/swagger-ui/index.html
```

Protected endpoints support JWT Bearer authentication through Swagger.

To test them:

1. Register a user.
2. Login.
3. Copy the returned access token.
4. Click **Authorize** in Swagger.
5. Enter the JWT.
6. Call the protected notification endpoints.

---

# Tests

Run backend tests from the project root.

Windows:

```powershell
.\mvnw.cmd test
```

Linux/macOS:

```bash
./mvnw test
```

The backend includes unit tests for:

- Notification service
- Email notification strategy
- SMS notification strategy
- Push notification strategy
- Notification sender factory

Current test suite:

```text
Tests run: 11
Failures: 0
Errors: 0
Skipped: 0
```

---

# Frontend Validation

From the `frontend` directory:

```bash
npm run lint
```

Build the production frontend:

```bash
npm run build
```

The production output is generated in:

```text
frontend/dist
```

---

# Project Structure

```text
notifications-api/
|
+-- src/
|   +-- main/
|   |   +-- java/
|   |   |   +-- com/anthony/notifications_api/
|   |   |       +-- auth/
|   |   |       +-- config/
|   |   |       +-- exception/
|   |   |       +-- notification/
|   |   |       +-- security/
|   |   |       +-- user/
|   |   |
|   |   +-- resources/
|   |
|   +-- test/
|
+-- frontend/
|   +-- src/
|   |   +-- api/
|   |   +-- components/
|   |   +-- App.jsx
|   |   +-- App.css
|   |   +-- main.jsx
|   |
|   +-- Dockerfile
|   +-- package.json
|
+-- Dockerfile
+-- docker-compose.yml
+-- pom.xml
+-- README.md
```

---

# Technical Decisions

## Strategy Pattern

Notification delivery behavior varies by channel. The Strategy Pattern isolates each implementation and makes the system easier to extend.

## JWT Authentication

JWT provides stateless authentication suitable for a REST API and avoids maintaining server-side sessions.

## BCrypt

Passwords are never stored as plain text. BCrypt provides adaptive password hashing with built-in salting.

## DTOs

Request and response DTOs keep the public API contract separate from persistence entities and prevent entities from being exposed directly.

## Ownership-Scoped Queries

Repository queries include the authenticated user's identity, helping prevent insecure direct object reference (IDOR) vulnerabilities.

## PostgreSQL

A relational database is appropriate because users and notifications have a clear one-to-many relationship and benefit from relational integrity.

## Docker Compose

The frontend, backend, and database can be started as one reproducible environment, reducing setup requirements for reviewers.

## React Without Additional State Libraries

The frontend is intentionally lightweight. React state, props, hooks, Fetch API, and localStorage are sufficient for the scope of this challenge without introducing unnecessary dependencies.

---

# Future Improvements

Given additional production requirements, possible improvements include:

- Refresh tokens
- HttpOnly cookie-based authentication
- Pagination and filtering
- Notification delivery status persistence
- Retry policies
- Asynchronous message processing
- Real email/SMS/push providers
- Integration tests
- Frontend component tests
- Database migrations with Flyway or Liquibase
- Production secret management
- Rate limiting
- Observability and metrics

---

# Author

**Carlos Anthony Cárdenas Segovia**