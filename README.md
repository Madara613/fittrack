# FitTrack

FitTrack is a modern, full-stack fitness tracking web application designed to help users monitor workouts, set personal fitness targets, and track daily progress toward their health goals. Built with a responsive single-page architecture and a robust RESTful backend, FitTrack enables users to seamlessly log workout activities, review comprehensive metrics such as active time and calories burned, manage profile data with automated BMI computation, and maintain continuous accountability on their fitness journey.

---

## 🌐 Live Demo

- **Application URL**: [https://fittrack-nu-steel.vercel.app](https://fittrack-nu-steel.vercel.app)
- **Demo Credentials**:
  - **Email**: `test@test.com`
  - **Password**: `test1234`

> **Note**: The backend is hosted on a free-tier cloud service (Render). Due to idle spin-down, the initial request or login may take up to a minute to wake up the server. Subsequent requests will run at normal speed.

---

## ✨ Features

- **Authentication & Security**: User registration and login powered by stateless JSON Web Tokens (JWT) and BCrypt password encryption.
- **Protected Routes**: Client-side route guards ensuring private dashboard, workout, and profile pages are accessible only to authenticated users.
- **Workout Logging**: Full CRUD capability for workout sessions (create, view list, inspect details, update, and delete entries) tracking activity type, duration, calories burned, and date.
- **Interactive Dashboard**: High-level visual statistics including total workouts logged, aggregate calories burned, and total active time.
- **User Profile & Fitness Targets**: Dedicated profile management for personal metrics (height, weight, goal weight, and weekly workout target).
- **Auto-Calculated BMI**: Real-time Body Mass Index (BMI) calculation and classification derived from recorded height and weight metrics.

---

## 🛠️ Tech Stack

### Frontend
- **Framework**: [React 19](https://react.dev/)
- **Bundler & Tooling**: [Vite](https://vitejs.dev/)
- **Styling**: [Tailwind CSS](https://tailwindcss.com/)
- **Icons**: [Lucide React](https://lucide.dev/)
- **Routing**: [React Router](https://reactrouter.com/)

### Backend
- **Language**: [Java 21](https://www.oracle.com/java/)
- **Framework**: [Spring Boot 3](https://spring.io/projects/spring-boot)
- **Security**: [Spring Security](https://spring.io/projects/spring-security) with JWT authentication
- **Data & Persistence**: [Spring Data JPA](https://spring.io/projects/spring-data-jpa) & Hibernate

### Database & Infrastructure
- **Database**: [PostgreSQL](https://www.postgresql.org/) hosted on [Supabase](https://supabase.com/)
- **Backend Deployment**: [Render](https://render.com/) (containerized via Docker multi-stage build)
- **Frontend Deployment**: [Vercel](https://vercel.com/)

---

## 📁 Project Structure

```text
fittrack/
├── backend/
│   ├── Dockerfile
│   ├── pom.xml
│   └── src/
│       └── main/
│           ├── java/com/fittrack/
│           │   ├── config/          # Security & CORS configuration
│           │   ├── controller/      # REST API controllers
│           │   ├── dto/             # Request & response data transfer objects
│           │   ├── entity/          # JPA entities (User, Workout, Profile)
│           │   ├── repository/      # Spring Data JPA repositories
│           │   ├── security/        # JWT filter & token provider
│           │   └── service/         # Business logic layer
│           └── resources/
│               └── application.properties
└── frontend/
    ├── index.html
    ├── package.json
    ├── vercel.json
    ├── vite.config.js
    └── src/
        ├── api/                     # Axios/fetch API client helpers
        ├── context/                 # AuthContext & global state
        ├── pages/                   # AuthPage, DashboardPage, ProfilePage
        ├── utils/                   # Helpers & calculation utilities
        ├── App.jsx
        └── main.jsx
```

---

## 🔌 API Endpoints

The backend exposes the following RESTful endpoints:

| Method | Endpoint | Access | Description |
|---|---|---|---|
| `POST` | `/auth/signup` | Public | Register a new user account |
| `POST` | `/auth/login` | Public | Authenticate user and receive JWT bearer token |
| `GET` | `/api/health` | Public | Service health check endpoint |
| `GET` | `/workouts` | Protected | List all logged workouts for the authenticated user |
| `POST` | `/workouts` | Protected | Create a new workout log entry |
| `GET` | `/workouts/{id}` | Protected | Retrieve details of a specific workout by ID |
| `PUT` | `/workouts/{id}` | Protected | Update an existing workout by ID |
| `DELETE` | `/workouts/{id}` | Protected | Delete a workout entry by ID |
| `GET` | `/profile` | Protected | Retrieve profile details and targets for the current user |
| `PUT` | `/profile` | Protected | Update profile measurements and goals (height, weight, etc.) |

---

## 💻 Run Locally

### Prerequisites
- **Java Development Kit (JDK)**: Version 21 or newer
- **Node.js**: Version 18+ and `npm`
- **PostgreSQL**: Local instance or remote PostgreSQL database (e.g., Supabase)

### 1. Backend Setup

1. Navigate to the `backend` directory:
   ```bash
   cd backend
   ```
2. Configure environment variables (via system environment or your local `.env` / run configuration):
   - `SPRING_DATASOURCE_URL`
   - `SPRING_DATASOURCE_USERNAME`
   - `SPRING_DATASOURCE_PASSWORD`
   - `JWT_SECRET`
   - `ALLOWED_ORIGINS` *(e.g., `http://localhost:5173`)*
3. Build and run the Spring Boot application using the Maven wrapper:
   - **Linux / macOS**:
     ```bash
     ./mvnw spring-boot:run
     ```
   - **Windows (PowerShell / Command Prompt)**:
     ```bash
     .\mvnw.cmd spring-boot:run
     ```
   The backend server will start on `http://localhost:8080`.

### 2. Frontend Setup

1. Navigate to the `frontend` directory:
   ```bash
   cd frontend
   ```
2. Create a `.env` file from `.env.example`:
   ```bash
   cp .env.example .env
   ```
3. Ensure the environment variable is configured:
   - `VITE_API_URL` *(defaults to `http://localhost:8080`)*
4. Install dependencies:
   ```bash
   npm install
   ```
5. Start the development server:
   ```bash
   npm run dev
   ```
   The client application will start at `http://localhost:5173`.

---

## 🚀 Deployment

- **Backend**: Containerized with a multi-stage Docker build (`maven:3.9-eclipse-temurin-21` to build, `eclipse-temurin:21-jre` to run) and deployed as a web service on **Render**. Environment variables and database credentials are injected securely through Render's environment dashboard.
- **Frontend**: Built and deployed on **Vercel** with automatic client-side route rewrites handled via `vercel.json`. The frontend communicates with the hosted backend over HTTPS using the `VITE_API_URL` environment variable.

---


## ☕ Java Concepts Used

This project relies on core Java and Spring Boot paradigms to implement its backend functionality.

### 1. Object-Oriented Programming (OOP)
- **Inheritance**: Custom domain exceptions like `ResourceNotFoundException` and `UnauthorizedException` extend standard Java's `RuntimeException`.
- **Interfaces & Polymorphism**: Security components like `CustomUserDetailsService` implement the standard `UserDetailsService` interface. Repositories (`WorkoutRepository`, `UserRepository`, etc.) extend Spring Data's `JpaRepository` interface.
- **Encapsulation**: Domain models (e.g., `User`, `Workout`, `Profile` entities) protect state with private fields. Access is granted through getter and setter methods auto-generated by Lombok (`@Getter`, `@Setter`).
- **Exception Handling**: A centralized global exception handler (`GlobalExceptionHandler` annotated with `@RestControllerAdvice`) catches custom exceptions and validation errors, mapping them cleanly to structured HTTP responses.

### 2. Collections and Generics
- **Lists**: Used to retrieve and pass multiple records (e.g., `List<WorkoutResponse>` in `WorkoutService` and `List<Workout>` returned by `WorkoutRepository`).
- **Maps**: Used for JWT token construction (`Map<String, Object> extraClaims` in `AuthService` and `JwtService`) and structuring validation error responses (`Map<String, String>`).
- **Optionals**: Used in repository query methods (e.g., `Optional<User> findByEmail(String email)` in `UserRepository`) to safely handle potentially missing database rows without throwing `NullPointerException`.
- **Generics**: Heavily leveraged in Spring Data JPA repository definitions (e.g., `JpaRepository<Workout, Long>`) to provide type-safe CRUD operations.

### 3. Multithreading
- **Server Threading**: The backend uses Spring Boot's embedded Tomcat web server. Tomcat automatically assigns a dedicated thread from its managed request pool to handle each incoming HTTP request concurrently.
- **Connection Pooling**: Database concurrency is handled automatically using the default HikariCP connection pool (configured with a maximum pool size in `application.properties`). 
- **Note**: The codebase relies entirely on the framework for concurrency. There are no explicit custom threads, `Runnable` implementations, or manual `synchronized` blocks.

### 4. Database Classes and JDBC
- **CRUD Operations**: Handled via Spring Data JPA repository interfaces (like `ProfileRepository` and `ProgressLogRepository`) and corresponding service classes. These provide high-level methods (`save()`, `findById()`, etc.) without requiring direct SQL writing.
- **Connectivity (JDBC)**: The application connects to a PostgreSQL database using the standard PostgreSQL JDBC driver (`org.postgresql.Driver`). Connection lifecycle is abstracted by HikariCP.
- **ORM (JPA/Hibernate)**: Hibernate maps plain Java objects (annotated with `@Entity`) directly to PostgreSQL database tables, automatically generating SQL queries on the fly based on repository method calls.


## 👤 Team Name

- **CloudCoders**
