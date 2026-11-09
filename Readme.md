# Concert Reservation API

A backend REST API for concert ticket reservation, built with Spring Boot 3.4 and PostgreSQL. The system handles the full reservation lifecycle with JWT authentication, role-based access control, and optimistic concurrency to prevent overbooking.

## Architecture

```
Client (Postman / Frontend)
    │
    ├── POST /auth/register, /auth/login  →  JWT Token
    │
    ├── Bearer Token in every request
    │
    ▼
┌─────────────────────────────────────────────────┐
│  JwtAuthenticationFilter                        │
│  Intercepts every request, validates token,     │
│  identifies user via SecurityContextHolder      │
└──────────────────┬──────────────────────────────┘
                   ▼
┌─────────────────────────────────────────────────┐
│  SecurityConfiguration                          │
│  Public:  GET /concerts, /auth/**               │
│  ADMIN:   POST/PUT/DELETE /concerts             │
│  USER:    POST /reservations, confirm, cancel   │
└──────────────────┬──────────────────────────────┘
                   ▼
┌─────────────────────────────────────────────────┐
│  Controllers → Services → Repositories → DB     │
│  DTOs in/out │ @Transactional │ Spring Data JPA │
│  @Valid      │ @Version       │ PostgreSQL      │
└─────────────────────────────────────────────────┘
```

## Key Technical Decisions

| Decision | Why |
|----------|-----|
| Optimistic locking (`@Version`) | Prevents overbooking without database-level locks — suited for low-to-moderate contention |
| Seats decremented at PENDING | Reserves the seat immediately; avoids selling the same seat twice during payment delay |
| DTOs for input and output | Entities never exposed to clients — no password hash, no internal version field in responses |
| User identity from JWT only | Server reads the token to know who reserves; client cannot impersonate another user |
| `@RestControllerAdvice` | Centralized exception-to-HTTP translation (404, 409, 400) instead of scattered try/catch |
| `existsByConcertAndStatut()` | Single SQL boolean query instead of loading all reservations in memory to check a condition |
| BCrypt password encoding | Passwords hashed before storage; compared at login without ever storing plaintext |

## Reservation Lifecycle

```
   ┌──────────┐     confirm      ┌───────────┐
   │ PENDING  │ ───────────────► │ CONFIRMED │
   └────┬─────┘                  └─────┬─────┘
        │         cancel               │ cancel
        ▼                              ▼
   ┌──────────┐                  ┌───────────┐
   │CANCELLED │ ◄──────────────  │ CANCELLED │
   └──────────┘                  └───────────┘

 • Seat decremented at PENDING creation
 • Seat restored on CANCEL (from any prior state)
 • Double cancel/confirm blocked by state guard
 • Owner-only: users can only act on their own reservations
```

## Concurrency Protection

```
Alice and Bob both try to reserve the last seat:

A: findById → concert (places=1, version=5)
B: findById → concert (places=1, version=5)
A: setPlaces(0) → UPDATE ... WHERE version=5 → success, version → 6
B: setPlaces(0) → UPDATE ... WHERE version=5 → 0 rows → OptimisticLockException
                                                         → no overbooking
```

## API Endpoints

### Auth (public)
`POST /api/v1/auth/register` · `POST /api/v1/auth/login`

### Concerts
`GET /api/v1/concerts` (public) · `GET /api/v1/concerts/{id}` (public) · `POST` · `PUT` · `DELETE` (ADMIN)

### Reservations
`POST /api/v1/concerts/{id}/reservations` (USER) · `PUT /api/v1/reservations/{id}/confirm` (owner) · `PUT /api/v1/reservations/{id}/cancel` (owner) · `GET /api/v1/reservations` (USER: own / ADMIN: all)

### User
`GET /api/v1/users/me` (authenticated)

## Tech Stack

Spring Boot 3.4 · Spring Security + JWT (jjwt) · Spring Data JPA · PostgreSQL · Gradle · Lombok · Bean Validation · JUnit 5 + Testcontainers

## Tests

7 integration tests running against a real PostgreSQL instance via Testcontainers (Docker). Tests cover seat decrement, reservation lifecycle, exception cases, and cancel with seat restoration.