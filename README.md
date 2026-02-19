# FixFlow — Building Maintenance Request System

> A full-stack web application for managing building maintenance requests with role-based workflows, built with Java/Spring Boot, Vue.js, and PostgreSQL.

[Add screenshot of Dashboard]

[Add screenshot of Request Form]

[Add screenshot of Request Detail]

---

## Tech Stack

| Layer | Technology | Version |
|-------|-----------|---------|
| Backend | Java + Spring Boot | Java 17, Spring Boot 3.2.5 |
| Frontend | Vue.js (Composition API) | Vue 3 + Vite 5 |
| Database | PostgreSQL | 15+ |
| ORM | Spring Data JPA + Hibernate | |
| Security | Spring Security + JWT (jjwt 0.12.5) | |
| Testing | JUnit 5 + Mockito | |
| HTTP Client | Axios | 1.6+ |
| State Management | Pinia | 2.1+ |
| Charts | Chart.js + vue-chartjs | 4.x |
| CSS Framework | Plain CSS with CSS variables | |
| Containerization | Docker + docker-compose | |
| Build Tool | Maven (backend), npm (frontend) | |

---

## Features

- **Role-based access control** — TENANT, STAFF, and ADMIN roles with different permissions
- **Maintenance request workflow** — OPEN → ASSIGNED → IN_PROGRESS → RESOLVED → CLOSED
- **JWT authentication** — Stateless API with auto-logout on token expiry
- **Staff assignment** — Admins can assign specific staff members to requests
- **Comment threads** — Both tenants and staff can leave updates on each request
- **Dashboard analytics** — Live charts showing requests by category and priority
- **Filterable request table** — Filter by status and priority
- **Seed data** — Default buildings and users created on first startup
- **Docker support** — Entire stack runs with `docker-compose up`

---

## Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+
- Node.js 18+
- PostgreSQL 15+ (or use Docker)

### Option A: Run with Docker (Recommended)

```bash
git clone https://github.com/yourusername/fixflow.git
cd fixflow
docker-compose up --build
```

- Frontend: http://localhost:3000
- Backend API: http://localhost:8080/api
- Database: localhost:5432

### Option B: Run Locally

**1. Set up PostgreSQL database:**

```sql
CREATE DATABASE fixflow_db;
CREATE USER fixflow_user WITH PASSWORD 'fixflow123';
GRANT ALL PRIVILEGES ON DATABASE fixflow_db TO fixflow_user;
```

**2. Run the backend:**

```bash
cd backend
mvn spring-boot:run
```

The backend starts at http://localhost:8080

**3. Run the frontend:**

```bash
cd frontend
npm install
npm run dev
```

The frontend starts at http://localhost:5173

---

## Default Credentials

| Role | Email | Password |
|------|-------|----------|
| Admin | admin@fixflow.com | admin123 |
| Staff | staff@fixflow.com | staff123 |
| Tenant | tenant@fixflow.com | tenant123 |

---

## API Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | /api/auth/register | Register a new user | Public |
| POST | /api/auth/login | Login, returns JWT | Public |
| GET | /api/requests | List all requests (optional ?status= filter) | STAFF, ADMIN |
| GET | /api/requests/my | List logged-in tenant's requests | TENANT |
| POST | /api/requests | Create a new request | TENANT |
| GET | /api/requests/{id} | Get request details | Authenticated |
| PATCH | /api/requests/{id}/status | Update request status | STAFF, ADMIN |
| PATCH | /api/requests/{id}/assign | Assign staff to request | ADMIN |
| POST | /api/requests/{id}/comments | Add a comment | Authenticated |
| GET | /api/requests/{id}/comments | Get comments for request | Authenticated |
| GET | /api/buildings | List all buildings | Authenticated |
| GET | /api/dashboard/stats | Dashboard statistics | STAFF, ADMIN |

---

## Database Schema

Four tables: `buildings`, `users`, `maintenance_requests`, `request_comments`

```
buildings (id, name, address, total_floors)
users (id, email, password_hash, full_name, role, building_id, created_at)
maintenance_requests (id, title, description, category, priority, status,
                      floor_number, room_number, building_id, created_by,
                      assigned_to, created_at, updated_at, resolved_at)
request_comments (id, request_id, user_id, comment_text, created_at)
```

---

## Project Structure

```
fixflow/
├── backend/
│   ├── src/main/java/com/fixflow/
│   │   ├── FixflowApplication.java
│   │   ├── DataSeeder.java          # Seeds DB on first startup
│   │   ├── config/                  # SecurityConfig, CorsConfig, JwtUtil
│   │   ├── controller/              # REST controllers
│   │   ├── service/                 # Business logic
│   │   ├── repository/              # Spring Data JPA interfaces
│   │   ├── model/                   # JPA entities
│   │   ├── dto/                     # Request/response DTOs
│   │   ├── exception/               # Custom exceptions + global handler
│   │   └── security/                # JWT filter, UserDetailsService
│   ├── src/test/                    # JUnit 5 tests
│   ├── pom.xml
│   └── Dockerfile
├── frontend/
│   ├── src/
│   │   ├── views/                   # LoginView, DashboardView, etc.
│   │   ├── components/              # Navbar, RequestCard, StatusBadge, etc.
│   │   ├── stores/                  # Pinia stores (auth, requests)
│   │   ├── services/api.js          # Axios instance with JWT interceptor
│   │   └── router/index.js          # Vue Router with navigation guards
│   ├── package.json
│   ├── vite.config.js
│   └── Dockerfile
├── docker-compose.yml
└── README.md
```

---

## Testing

```bash
cd backend
mvn test
```

Tests use an H2 in-memory database (no PostgreSQL needed for tests).

**Test coverage:**
- `MaintenanceRequestServiceTest` — 7 unit tests with Mockito
- `MaintenanceRequestControllerTest` — 6 integration tests with MockMvc
- `MaintenanceRequestRepositoryTest` — 4 repository slice tests

---

## Status Workflow

```
OPEN → ASSIGNED → IN_PROGRESS → RESOLVED → CLOSED
           ↑          |
           └──────────┘  (can be re-assigned if needed)
```

- **OPEN**: Newly submitted, awaiting assignment
- **ASSIGNED**: Staff member assigned, not yet started
- **IN_PROGRESS**: Staff is actively working on it
- **RESOLVED**: Issue fixed, awaiting confirmation
- **CLOSED**: Fully closed (terminal state)

---

## Future Improvements

- [ ] File/photo upload for maintenance requests
- [ ] Email notifications on status changes (Spring Mail)
- [ ] WebSocket real-time updates (STOMP + SockJS)
- [ ] CI/CD pipeline with GitHub Actions
- [ ] Tenant satisfaction rating after closure
- [ ] PDF report generation for building managers
- [ ] Mobile app (Vue Native or Capacitor)

---

*Built with Java + Spring Boot + Vue.js + PostgreSQL*
