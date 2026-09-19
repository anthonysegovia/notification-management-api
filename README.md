# Notification Management API

A RESTful notification management API built with **Java 21, Spring Boot, Spring Security, PostgreSQL, JWT, and Docker**.

The application allows authenticated users to create, retrieve, update, and delete their own notifications. Notifications can be sent through different channels using an extensible Strategy-based architecture.

## Features

- User registration and authentication
- JWT-based stateless authentication
- BCrypt password hashing
- User-owned notification CRUD
- Email, SMS, and Push notification channels
- Strategy Pattern for notification delivery
- Channel-specific validation
- PostgreSQL persistence with Spring Data JPA
- Request validation
- Centralized exception handling
- OpenAPI / Swagger documentation
- Unit tests with JUnit and Mockito
- Dockerized application and database

## Tech Stack

- Java 21
- Spring Boot
- Spring Web
- Spring Security
- Spring Data JPA
- PostgreSQL
- JWT (JJWT)
- Bean Validation
- JUnit 5
- Mockito
- Springdoc OpenAPI / Swagger UI
- Maven
- Docker
- Docker Compose

## Architecture

The application follows a layered architecture:

```text
HTTP Request
     |
     v
Spring Security / JWT Filter
     |
     v
Controller
     |
     v
Service
   /     \
  v       v
Repository   NotificationSenderFactory
  |                   |
  v                   v
PostgreSQL       NotificationSender
                  /     |     \
                 v      v      v
               Email   SMS    Push
```

The main responsibilities are separated into:

- **Controller:** HTTP request and response handling.
- **Service:** Business logic and authorization rules.
- **Repository:** Database access through Spring Data JPA.
- **Security:** JWT authentication and Spring Security configuration.
- **Notification Senders:** Channel-specific notification behavior.
- **Exception Handler:** Centralized API error responses.

## Notification Channel Design

Notification delivery uses the **Strategy Pattern**.

All notification channels implement the same contract:

```java
public interface NotificationSender {

    NotificationChannel getChannel();

    void send(Notification notification);
}
```

Current implementations:

- `EmailNotificationSender`
- `SmsNotificationSender`
- `PushNotificationSender`

`NotificationSenderFactory` receives all available implementations through Spring Dependency Injection and selects the appropriate strategy according to the notification channel.

This avoids placing channel-specific logic inside `NotificationService`.

For example, adding a new notification channel primarily requires creating another `NotificationSender` implementation rather than modifying the notification delivery flow.

This follows the **Open/Closed Principle** by keeping the system open for extension while minimizing modifications to existing business logic.

## Channel Rules

### Email

- Validates the recipient email format.
- Generates an email template containing the subject and content.
- Simulates delivery through application logs.

### SMS

- Validates the recipient phone number.
- Limits the notification content to a maximum of 160 characters.
- Logs the recipient number, date, and content.

### Push

- Validates the device token.
- Generates a Push payload.
- Logs the simulated delivery status.

Notification delivery is simulated. No external email, SMS, or Push provider is required.

## Authentication

Authentication uses JWT.

### Registration

```http
POST /api/auth/register
```

Example:

```json
{
  "email": "user@example.com",
  "password": "Password123"
}
```

Passwords are hashed using BCrypt before being persisted.

### Login

```http
POST /api/auth/login
```

Example:

```json
{
  "email": "user@example.com",
  "password": "Password123"
}
```

A successful login returns:

```json
{
  "accessToken": "eyJ..."
}
```

Protected endpoints require:

```http
Authorization: Bearer <JWT>
```

The API uses stateless authentication and does not maintain server-side login sessions.

## Authorization

Notifications are scoped to the authenticated user.

The API does not trust a client-provided user ID when accessing notifications. Instead, the user's identity is obtained from the validated JWT.

Repository queries include the authenticated user's email:

```java
findByIdAndUserEmail(id, email)
```

Therefore, knowing another notification's ID is not enough to retrieve, modify, or delete it.

A notification that does not exist or does not belong to the authenticated user is treated as not found.

## Notification API

All notification endpoints require authentication.

### Create notification

```http
POST /api/notifications
```

Example Email request:

```json
{
  "title": "Welcome",
  "content": "Welcome to the application!",
  "channel": "EMAIL",
  "recipient": "user@example.com"
}
```

Example SMS request:

```json
{
  "title": "Verification",
  "content": "Your verification code is 123456",
  "channel": "SMS",
  "recipient": "+528112345678"
}
```

Example Push request:

```json
{
  "title": "New message",
  "content": "You have a new notification",
  "channel": "PUSH",
  "recipient": "device-token-abc123"
}
```

### List authenticated user's notifications

```http
GET /api/notifications
```

### Get notification

```http
GET /api/notifications/{id}
```

### Update notification

```http
PUT /api/notifications/{id}
```

### Delete notification

```http
DELETE /api/notifications/{id}
```

Successful deletion returns:

```text
204 No Content
```

## Error Handling

The API uses centralized exception handling through `@RestControllerAdvice`.

Typical responses include:

| Scenario | HTTP Status |
|---|---:|
| Invalid request | 400 Bad Request |
| Invalid notification data | 400 Bad Request |
| Missing or invalid authentication | 401 Unauthorized |
| Resource not found | 404 Not Found |
| Email already registered | 409 Conflict |

Example:

```json
{
  "timestamp": "2026-09-19T04:45:12",
  "status": 404,
  "error": "Not Found",
  "message": "Notification not found",
  "path": "/api/notifications/999"
}
```

## Running with Docker

### Requirements

Only the following is required:

- Docker
- Docker Compose

Clone the repository and run:

```bash
docker compose up --build
```

Docker Compose starts:

- Spring Boot API
- PostgreSQL database

The API will be available at:

```text
http://localhost:8080
```

Swagger UI:

```text
http://localhost:8080/swagger-ui/index.html
```

To stop the application:

```bash
docker compose down
```

To also remove the PostgreSQL volume:

```bash
docker compose down -v
```

## Running Locally

Requirements:

- Java 21
- Docker

Start PostgreSQL:

```bash
docker compose up postgres -d
```

The development PostgreSQL instance is exposed on:

```text
localhost:5433
```

Then run the application:

### Windows

```powershell
.\mvnw.cmd spring-boot:run
```

### Linux / macOS

```bash
./mvnw spring-boot:run
```

## Configuration

The application supports environment variables for runtime configuration.

| Variable | Purpose |
|---|---|
| `DB_URL` | PostgreSQL JDBC URL |
| `DB_USERNAME` | Database username |
| `DB_PASSWORD` | Database password |
| `JWT_SECRET` | JWT signing secret |
| `JWT_EXPIRATION` | JWT expiration in milliseconds |

Development defaults are defined in `application.properties`.

Production environments should provide their own credentials and JWT secret through environment variables or a secrets-management solution.

## Swagger / OpenAPI

Interactive API documentation is available at:

```text
http://localhost:8080/swagger-ui/index.html
```

To test protected endpoints:

1. Register a user using `/api/auth/register`.
2. Login using `/api/auth/login`.
3. Copy the returned `accessToken`.
4. Click **Authorize** in Swagger.
5. Enter the JWT.
6. Execute the notification endpoints.

Swagger automatically sends:

```http
Authorization: Bearer <JWT>
```

for authenticated requests.

## Tests

Run all automated tests with:

### Windows

```powershell
.\mvnw.cmd test
```

### Linux / macOS

```bash
./mvnw test
```

The current test suite covers key business and architectural behavior, including:

- Valid Email recipient
- Invalid Email recipient
- SMS content up to 160 characters
- SMS content exceeding 160 characters
- Valid Push device token
- Invalid Push device token
- Correct notification strategy selection
- Access to notifications belonging to the authenticated user
- Rejection of access to another user's notification

## Database Model

The main relationship is:

```text
User 1 ─────────── N Notification
```

A notification belongs to exactly one user.

Simplified schema:

```text
users
----------------
id          PK
email       UNIQUE
password


notifications
----------------
id          PK
title
content
channel
recipient
created_at
user_id     FK -> users.id
```

`channel` is stored using the enum name (`EMAIL`, `SMS`, or `PUSH`) rather than its ordinal position to avoid persistence issues if the enum ordering changes.

## Technical Decisions

### JWT and Stateless Authentication

JWT was selected to keep authentication stateless. Each protected request carries its authentication information through the `Authorization` header.

### BCrypt

Passwords are never stored in plain text. BCrypt handles password hashing and salting, and authentication uses `PasswordEncoder.matches()`.

### DTOs

API requests and responses use DTOs instead of exposing JPA entities directly. This prevents persistence details and sensitive fields such as password hashes from leaking through the API.

### Strategy Pattern

Channel-specific behavior is isolated behind `NotificationSender`, preventing `NotificationService` from becoming dependent on Email, SMS, Push, or future channel implementations.

### PostgreSQL

A relational database fits the relationship between users and their notifications and provides database-level integrity through primary keys, unique constraints, and foreign keys.

### Centralized Error Handling

`@RestControllerAdvice` provides consistent HTTP error responses without duplicating exception-handling logic across controllers.

## Possible Future Improvements

Given additional production requirements, the application could be extended with:

- Refresh tokens
- Token revocation
- Database migrations with Flyway or Liquibase
- Pagination and filtering
- Asynchronous notification processing
- Message queues
- Real Email/SMS/Push providers
- Delivery status persistence
- Retry policies and dead-letter handling
- Rate limiting
- Observability and metrics
- Integration and end-to-end tests
- CI/CD pipeline

## Author

**Carlos Anthony Cárdenas Segovia**