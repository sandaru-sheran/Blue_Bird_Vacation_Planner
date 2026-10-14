# Blue Bird Vacation Planner — Backend API

![Java](https://img.shields.io/badge/Java-21%20LTS-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-6DB33F?style=for-the-badge&logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white)
![Database](https://img.shields.io/badge/Database-MySQL%20%7C%20PostgreSQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)
![Deployment](https://img.shields.io/badge/Deployed%20on-Render-46E3B7?style=for-the-badge&logo=render&logoColor=black)

A RESTful backend service powering the **Blue Bird Vacation Planner**, a web application designed to help day-trippers discover verified places of interest around the Nupe/Matara region, check destination weather forecasts, explore nearby dining insights, and calculate realistic multi-stop travel routes.

Built with **Java 21 LTS** and **Spring Boot**, this service implements stateless **JWT authentication**, role-based access control (RBAC), automated database seeding, and third-party API integrations for routing and meteorology.

---

## Live Deployment & Links

| Resource | URL |
| :--- | :--- |
| **Frontend Application (Vercel)** | `https://your-frontend-url.vercel.app` *(Replace before submission)* |
| **Backend Base URL (Render)** | `https://your-backend-url.onrender.com` *(Replace before submission)* |
| **API Health Check** | `https://your-backend-url.onrender.com/api/health` |
| **Swagger UI / OpenAPI Docs** | `https://your-backend-url.onrender.com/swagger-ui/index.html` |
| **Frontend GitHub Repository** | `https://github.com/your-username/frontend-repo` |

> **Note on Cold Starts:** The backend is hosted on Render's free tier, which spins down after 15 minutes of inactivity. The initial API request or login attempt may take approximately 45–60 seconds while the container wakes up.

---

## Key Features

### 1. Security & Administrator Management
* **Stateless JWT Authentication:** Custom `JwtFilter` and `JwtServiceImpl` issuing signed HMAC-SHA tokens containing role and subject claims.
* **High-Strength Password Hashing:** Utilizes `BCryptPasswordEncoder` with a work factor (strength) of `14` to protect administrator credentials at rest.
* **Automated Admin Provisioning:** Includes an `AdminSeeder` (`CommandLineRunner`) that automatically provisions initial administrator accounts from environment configurations if the database is empty on startup.
* **Secure Password Rotation:** Authenticated administrators can update their passwords (`PUT /api/auth/update-password`) with strict regex enforcement (uppercase, lowercase, numeric, and special characters required).

### 2. Tourist & Itinerary Services (Public Access)
* **Categorized Destination Discovery:** Browse and filter verified locations across four core categories: `RELIGIOUS`, `NATURE`, `HERITAGE`, and `LEISURE`.
* **Smart Multi-Stop Route Calculation:** Integrates with the **OpenRouteService (HeiGIT) Directions API** (`driving-car` profile) to compute total distance, raw travel duration, and encoded overview polylines.
* **Automated Buffer-Time Logic:** Automatically injects a realistic 15-minute ($900\text{ seconds}$) travel buffer per transition between stops:
  $$T_{\text{optimized}} = T_{\text{raw}} + (N - 1) \times 900\text{ s}$$
  where $N$ is the number of selected destinations in the day plan.
* **7-Day Weather Forecasting:** Integrates with the **Open-Meteo API** to provide daily temperature averages and WMO weather condition descriptions for any valid travel date within the upcoming 7-day window.
* **Nearby Dining Insights:** Curated restaurant recommendations, price ranges, and signature dishes linked to specific destinations.

---

## Tech Stack

* **Language:** Java 21 LTS
* **Framework:** Spring Boot 3.x (Spring Web, Spring Data JPA, Spring Validation)
* **Security:** Spring Security 6, JSON Web Tokens (`io.jsonwebtoken` / JJWT)
* **Database:** MySQL 8.0 / PostgreSQL (compatible via Hibernate/JPA)
* **API Documentation:** Springdoc OpenAPI 3 (Swagger UI)
* **External APIs:**
  * [OpenRouteService API (HeiGIT)](https://openrouteservice.org/) — Route geometry, distance, and duration calculation.
  * [Open-Meteo API](https://open-meteo.com/) — Coordinate-based daily weather forecasting.
* **Utilities:** Project Lombok, Jackson Databind, Maven Wrapper (`mvnw`)

---

## Project Architecture

```text
src/main/java/com/sandaru/blue_bird_vacation_planner
├── config/                 # Application beans, CORS policy, and Spring Security filter chain
│   ├── AppConfig.java
│   ├── CorsConfig.java
│   └── SecurityConfig.java
├── controller/             # REST API controllers (Public & @PreAuthorize Admin endpoints)
│   ├── AuthController.java
│   ├── DestinationController.java
│   ├── DiningController.java
│   ├── HealthController.java
│   ├── RouteController.java
│   └── WeatherController.java
├── dto/                    # Immutable Java records and data transfer objects with Jakarta validation
├── entity/                 # JPA entities (Administrator, Destination, DiningInsight, DestinationCategory)
├── exception/              # Global @RestControllerAdvice exception handling
│   └── GlobalExceptionHandler.java
├── filter/                 # OncePerRequestFilter for Bearer token extraction and validation
│   └── JwtFilter.java
├── repository/             # Spring Data JPA repositories
├── seeder/                 # Startup CommandLineRunner for initial Admin account creation
│   └── AdminSeeder.java
└── service/                # Business logic interfaces and implementations
    └── impl/
```

---

## API Endpoints Reference

### Health & Documentation
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/health` | Public | Returns service health status (`UP`). |
| `GET` | `/swagger-ui/index.html` | Public | Interactive OpenAPI / Swagger documentation. |

### Authentication (`/api/auth`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/auth/login` | Public | Authenticates an admin and returns a Bearer JWT. |
| `PUT` | `/api/auth/update-password` | `ROLE_ADMIN` | Validates current password, hashes new password, and issues a fresh JWT. |

### Destinations (`/api/destinations`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/destinations` | Public | Lists all destinations. Optional query param: `?category=NATURE` |
| `GET` | `/api/destinations/{id}` | Public | Retrieves details for a single destination by ID. |
| `POST` | `/api/destinations` | `ROLE_ADMIN` | Creates a new destination profile. |
| `PUT` | `/api/destinations/{id}` | `ROLE_ADMIN` | Updates an existing destination profile. |
| `DELETE` | `/api/destinations/{id}` | `ROLE_ADMIN` | Deletes a destination by ID. |

### Dining Insights (`/api/dining`)
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/dining/nearby/{destinationId}` | Public | Lists dining recommendations associated with a destination. |
| `POST` | `/api/dining` | `ROLE_ADMIN` | Adds a new dining insight to a destination. |
| `PUT` | `/api/dining/{id}` | `ROLE_ADMIN` | Updates an existing dining insight. |
| `DELETE` | `/api/dining/{id}` | `ROLE_ADMIN` | Deletes a dining insight by ID. |

### Route & Weather Planning
| Method | Endpoint | Access | Description |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/route/calculate` | Public | Calculates route across $\ge 2$ destination IDs with 15-min buffers. |
| `GET` | `/api/weather` | Public | Fetches forecast. Query params: `lat`, `lon`, `travelDate` (`YYYY-MM-DD`). |

---

## Sample Request & Response Payloads

### 1. Administrator Login (`POST /api/auth/login`)
**Request Body:**
```json
{
  "username": "admin",
  "password": "SafePassword@123"
}
```
**Response (`200 OK`):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "type": "Bearer",
  "username": "admin"
}
```

### 2. Calculate Day Plan Route (`POST /api/route/calculate`)
**Request Body:**
```json
{
  "destinationIds": [1, 4, 7]
}
```
**Response (`200 OK`):**
```json
{
  "totalOptimizedTimeSeconds": 3940,
  "totalDistanceMeters": 18450,
  "overviewPolyline": "}_ilA~{...encoded_geometry...",
  "waypointOrder": [0],
  "rawJsonResponse": "{...}"
}
```

---

## Configuration & Environment Variables

To run this application locally or deploy it to **Render**, configure the following properties in your `application.properties` / `application.yml` or via system environment variables:

| Property / Environment Variable | Description | Example Value |
| :--- | :--- | :--- |
| `SPRING_DATASOURCE_URL` | JDBC connection string for MySQL or PostgreSQL | `jdbc:mysql://localhost:3306/blue_bird_db` |
| `SPRING_DATASOURCE_USERNAME` | Database username | `root` |
| `SPRING_DATASOURCE_PASSWORD` | Database password | `your_db_password` |
| `JWT_SECRET_KEY` | Secret key for HMAC-SHA signing (minimum 32 characters / 256-bit) | `your_super_secure_32_byte_secret_key_here` |
| `JWT.EXPIRES_IN_HOURS` | Token validity duration in hours (`JWT_EXPIRES_IN_HOURS` in env) | `24` |
| `maps.api.key` | OpenRouteService API Key (`MAPS_API_KEY` in env) | `5b3ce3597851110001cf6248...` |
| `app.admin.credentials` | SpEL Map of initial admin accounts seeded when DB is empty | `{'admin':'Admin@1234'}` |

### Example `application.properties` Setup
```properties
# Server & JPA Configuration
spring.application.name=blue-bird-vacation-planner
server.port=${PORT:8080}
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# Database Configuration
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/blue_bird_db?createDatabaseIfNotExist=true}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:root}

# JWT Configuration
JWT_SECRET_KEY=${JWT_SECRET_KEY:replace_with_a_very_long_and_secure_secret_key_at_least_32_bytes}
JWT.EXPIRES_IN_HOURS=${JWT_EXPIRES_IN_HOURS:24}

# External API Keys
maps.api.key=${MAPS_API_KEY:your_openrouteservice_api_key}

# Admin Seeder Map (SpEL format)
app.admin.credentials=${APP_ADMIN_CREDENTIALS:{'admin':'Admin@1234'}}
```

---

## Local Setup & Installation

### Prerequisites
* **Java 21 LTS** installed (`java -version`)
* **MySQL 8.0+** or **PostgreSQL** running locally
* An API key from [OpenRouteService](https://openrouteservice.org/dev/#/signup) (free tier)

### Step-by-Step Guide

1. **Clone the repository:**
   ```bash
   git clone https://github.com/your-username/blue-bird-vacation-planner-backend.git
   cd blue-bird-vacation-planner-backend
   ```

2. **Configure environment variables:**
   Set the required environment variables in your IDE/terminal or update `src/main/resources/application.properties` with your local database credentials and OpenRouteService API key.

3. **Build and run the project using the Maven Wrapper:**
   * On Linux/macOS:
     ```bash
     ./mvnw clean spring-boot:run
     ```
   * On Windows (Command Prompt / PowerShell):
     ```cmd
     mvnw.cmd clean spring-boot:run
     ```

4. **Verify the application is running:**
   * Health Check: [http://localhost:8080/api/health](http://localhost:8080/api/health)
   * Swagger UI: [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html)

---

## Error Handling

All API exceptions are intercepted globally by `GlobalExceptionHandler` and returned in a standardized JSON structure (`ApiErrorResponse`):

```json
{
  "timestamp": "2026-10-15T10:15:30.123456",
  "status": 400,
  "error": "Bad Request",
  "message": "At least two destinations are required to calculate a route.",
  "path": "/api/route/calculate"
}
```

---

## Author

* **N. G. Sandaru Sheran**
* **Project:** Blue Bird Vacation Planner (Full-Stack Engineering Assessment & Module ITE2953)