# Order Management System

## Overview

A secure Spring Boot REST API for product catalog administration, inventory-safe customer ordering, fulfillment, cancellations, and operational reporting.

## Features

- Customer self-registration plus admin, staff, and customer authorization
- Product creation, editing, activation, stock replenishment, search, and pagination
- Atomic multi-item checkout with deterministic pessimistic product locks to prevent overselling
- Customer-scoped order history; staff confirmation, fulfillment, cancellation, and dashboard metrics
- Immutable unit-price snapshots, stock restoration on cancellation, audit timestamps, health checks, Docker, and CI

## Technology Stack

Java 25, Spring Boot 4.1.1, Web MVC, Security, Data JPA, Validation, Flyway, PostgreSQL 17, H2 tests, and Maven Wrapper.

## Requirements and Installation

Use Java 25 and PostgreSQL 15+, or Docker. Copy `.env.example` to `.env`, configure secrets, then run `docker compose up --build` or `.\mvnw.cmd spring-boot:run`.

## Environment Variables

Required in production: `DATABASE_URL`, `DATABASE_USERNAME`, and `DATABASE_PASSWORD`. Optional: `TOKEN_TTL_MINUTES`, `ALLOWED_ORIGINS`, and `SPRING_PROFILES_ACTIVE`.

## Database and Tests

Flyway applies the PostgreSQL schema at startup. Run `.\mvnw.cmd verify`; CI also starts the packaged application against PostgreSQL and checks `/actuator/health`.

## Default Development Accounts

With the `dev` profile: `admin@example.com / AdminPassword123!`, `staff@example.com / StaffPassword123!`, and `customer@example.com / CustomerPassword123!`. These are development-only credentials.

## API Endpoints

- `POST /api/auth/register`, `/api/auth/login`, `/api/auth/logout`
- `GET|POST /api/products`, `PATCH /api/products/{id}`, `POST /api/products/{id}/stock`
- `GET|POST /api/orders`, `GET /api/orders/{id}`
- `PATCH /api/orders/{id}/confirm`, `/fulfill`, `/cancel`
- `GET /api/dashboard`, `GET /actuator/health`

All API responses use a `data` envelope; errors use a stable `error` envelope. List endpoints accept zero-based `page` and bounded `size` parameters.

## Security Notes

Passwords use BCrypt cost 12. Login returns a random 256-bit bearer token; only its SHA-256 digest is stored, and logout revokes it. The service also provides login throttling and timing equalization, DTO validation, customer ownership checks, allow-listed CORS, bounded pages, safe errors, parameterized JPA, row locks, optimistic versions, and database constraints.

## Known Limitations

Payment processing, shipping-carrier integration, tax calculation, returns, promotions, warehouse allocation, email, MFA, and password recovery are outside this portfolio scope.

## License

MIT
