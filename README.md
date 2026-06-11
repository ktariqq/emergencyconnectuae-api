<div align="center">

# EmergencyConnectUAE

**A secure, distributed emergency coordination platform built for UAE public safety infrastructure.**  
Real-time incident dispatch · Redis-backed concurrency · JWT + MFA authentication

<br/>

![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.5-7c3aed?style=flat-square&labelColor=1a0a2e&logo=springboot&logoColor=white)
![React](https://img.shields.io/badge/React_18-TypeScript-7c3aed?style=flat-square&labelColor=1a0a2e&logo=react&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-7.0-7c3aed?style=flat-square&labelColor=1a0a2e&logo=redis&logoColor=white)
![MySQL](https://img.shields.io/badge/MySQL-8.0-7c3aed?style=flat-square&labelColor=1a0a2e&logo=mysql&logoColor=white)
![JWT](https://img.shields.io/badge/JWT-Auth-7c3aed?style=flat-square&labelColor=1a0a2e&logo=jsonwebtokens&logoColor=white)
![HTTPS](https://img.shields.io/badge/HTTPS-SSL%2FTLS-7c3aed?style=flat-square&labelColor=1a0a2e&logo=letsencrypt&logoColor=white)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI_3-7c3aed?style=flat-square&labelColor=1a0a2e&logo=swagger&logoColor=white)
![License](https://img.shields.io/badge/License-MIT-7c3aed?style=flat-square&labelColor=1a0a2e)

</div>

<div align="center">

━━━━━━━━━━━━━━ ✦ ✧ ✦ ━━━━━━━━━━━━━━

</div>

## 🟣 Overview

EmergencyConnectUAE™ is a production-style distributed REST API platform designed to coordinate emergency response operations across the UAE. Built for CSC408 (Distributed Information Systems), it handles concurrent incident reporting, real-time unit dispatching, and multi-role secure access under high load — using Redis for caching, distributed locking, and session management.

The frontend is a React/TypeScript dashboard with a dark purple glass-morphism UI, providing live views of active incidents, available units, and assignment workflows.

<br><br>

## 🟣 Features

**Core Platform**
- Multi-role user system: Dispatcher, Responder, Hospital Admin, Admin
- Emergency incident lifecycle: OPEN → IN_PROGRESS → RESOLVED
- Emergency unit management: Ambulances, Fire Units, Police Units, Rescue Teams
- Region-aware assignment with audit trail on every action

**Security**
- HTTPS with SSL/TLS (PKCS12 keystore)
- JWT authentication with role-based access control
- Multi-Factor Authentication (TOTP/OTP) for Dispatcher role
- Rate limiting per IP with role-aware thresholds
- HTTP security headers: HSTS, X-Frame-Options, CSP, X-Content-Type-Options
- CORS whitelist enforcement
- Input validation and injection prevention
- No hardcoded secrets — environment variable configuration

**Redis (3 mandatory roles)**
- **Caching** — active incidents and available units cached with TTL and invalidation
- **Distributed Locks** — Redisson-based locks on unit assignment to eliminate race conditions
- **Session Storage** — JWT sessions stored in Redis, stateless backend, horizontally scalable

**API**
- RESTful, stateless, fully paginated collection endpoints
- Swagger/OpenAPI 3 documentation with JWT auth integration
- Global exception handling with consistent error responses

<br><br>

## 🟣 Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17, Spring Boot 3.2.5 |
| Security | Spring Security, JJWT 0.11.5 |
| Database | MySQL 8.0, Spring Data JPA |
| Cache / Locks / Sessions | Redis 7, Redisson, Spring Session |
| Frontend | React 18, TypeScript, Vite |
| API Docs | SpringDoc OpenAPI / Swagger UI |
| Build | Maven 3.9 |

<br><br>

## 🟣 Project Structure

```
emergencyconnectuae/
├── src/main/java/com/rest/emergencyconnectuae/
│   ├── models/          # Incident, EmergencyUnit, Assignment, User, AuditLog
│   ├── repo/            # JPA repositories with pagination
│   ├── impl/            # Service layer — business logic
│   ├── controllers/     # REST controllers + global exception handler
│   ├── security/        # JwtUtil, JwtFilter, SecurityConfig
│   ├── redis/           # CacheService, LockService, SessionService, OtpService
│   ├── filters/         # RateLimitFilter
│   └── config/          # RedisConfig, SwaggerConfig
├── src/main/resources/
│   └── application.properties
└── frontend/            # React/TypeScript dashboard
    └── src/
        ├── api/         # Axios instance with JWT interceptor
        ├── components/  # Navbar
        └── pages/       # Login, Dashboard, Incidents, Units, Assignments
```

<br><br>

## 🟣 Getting Started

### Prerequisites

- Java 17+
- Maven 3.9+
- MySQL 8.0+
- Redis 7+ (or Docker)
- Node.js 20+

### 1. Clone the repository

```bash
git clone https://github.com/ktariqq/emergencyconnectuae-api.git
cd emergencyconnectuae-api
```

### 2. Set up MySQL

```sql
CREATE DATABASE emergencyconnectuae;
CREATE USER 'user'@'localhost' IDENTIFIED BY 'user';
GRANT ALL PRIVILEGES ON emergencyconnectuae.* TO 'user'@'localhost';
```

### 3. Start Redis

```bash
# Docker
docker run -d --name redis -p 6379:6379 redis:7
```

### 4. Generate SSL certificate

```bash
cd src/main/resources
keytool -genkeypair -alias emergencyconnect -keyalg RSA -keysize 2048 \
  -storetype PKCS12 -keystore keystore.p12 -validity 365 \
  -storepass yourssl123 \
  -dname "CN=EmergencyConnectUAE, OU=CSC408, O=ADU, L=AbuDhabi, C=AE"
```

### 5. Set environment variables

```bash
export SSL_PASSWORD=yourssl123
export DB_USERNAME=ecuser
export DB_PASSWORD=yourpassword
export JWT_SECRET=your-secret-key-minimum-32-characters-long
```

### 6. Run the backend

```bash
mvn clean install -DskipTests
mvn spring-boot:run
```

API runs at `https://localhost:8443`  
Swagger UI: `https://localhost:8443/swagger-ui.html`

### 7. Run the frontend

```bash
cd frontend
npm install
npm run dev
```

Dashboard at `https://localhost:5173`

<br><br>

## 🟣 API Endpoints

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/api/auth/register` | Public | Register user |
| POST | `/api/auth/login` | Public | Login (OTP flow for Dispatcher) |
| POST | `/api/auth/verify-otp` | Public | Complete MFA for Dispatcher |
| GET | `/api/incidents` | JWT | All incidents (paginated) |
| POST | `/api/incidents` | JWT | Report new incident |
| GET | `/api/incidents/active` | JWT | Active incidents (cached) |
| PUT | `/api/incidents/{id}/status` | JWT | Update incident status |
| GET | `/api/units` | JWT | All units (paginated) |
| POST | `/api/units` | JWT | Register unit |
| GET | `/api/units/available` | JWT | Available units (cached) |
| POST | `/api/assignments` | JWT | Assign unit (distributed lock) |
| GET | `/api/assignments` | JWT | Assignment history |
| GET | `/api/audit` | ADMIN | Audit logs |

Full documentation available at `/swagger-ui.html`.

<br><br>

## 🟣 Redis Architecture

```
┌─────────────────────────────────────────────┐
│                  Redis                        │
│                                               │
│  Caching          incidents:active (TTL 60s)  │
│                   units:available  (TTL 30s)  │
│                                               │
│  Distributed      lock:unit:{id}              │
│  Locks            (Redisson, lease 10s)       │
│                                               │
│  Session          session:{uuid}              │
│  Storage          otp:{username} (TTL 300s)   │
└─────────────────────────────────────────────┘
```

Without distributed locks, two simultaneous assignment requests for the same unit would both succeed — resulting in double deployment. Redisson's `tryLock` ensures only one request proceeds; the other receives a clear error.

<br><br>

## 🟣 Security Architecture

```
Request → RateLimitFilter → JwtFilter → SecurityConfig (RBAC) → Controller
                ↓                ↓
           Redis rate         Validates JWT
           counter/IP         extracts role
```

MFA flow for Dispatcher:

```
POST /login → validates credentials → generates OTP → stores in Redis (5 min TTL)
           → returns "OTP_REQUIRED"
POST /verify-otp → checks OTP in Redis → deletes OTP (single use) → returns JWT
```

<br><br>


<div align="center">
<sub>Built with Spring Boot · Redis · React · TypeScript</sub>
</div>
