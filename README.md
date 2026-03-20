# Golf Charity Subscription Platform

Production-ready full-stack implementation for the Digital Heroes PRD.

## Tech Stack

- Backend: Java Spring Boot 3, Spring Security JWT, Spring Data JPA, PostgreSQL
- Frontend: React + Vite + Tailwind CSS + Axios + Framer Motion
- Deployment: Render (backend), Vercel (frontend), Supabase Postgres (database)

## Project Structure

- `backend` Spring Boot API
- `frontend` React app
- `docs` architecture and deployment documentation

## Backend Quick Start

1. Go to backend folder:
   - `cd backend`
2. Configure env vars from `.env.example`.
3. Start PostgreSQL and create database `golf_charity`.
4. Run app:
   - `mvn spring-boot:run`

Backend base URL: `http://localhost:8080/api/v1`
Swagger UI: `http://localhost:8080/swagger-ui`

## Frontend Quick Start

1. Go to frontend folder:
   - `cd frontend`
2. Install dependencies:
   - `npm install`
3. Copy `.env.example` to `.env` and set `VITE_API_URL`.
4. Start app:
   - `npm run dev`

Frontend URL: `http://localhost:5173`

## Automated Tests

Backend integration tests are available and run with:

- `cd backend`
- `mvn test`

Covered scenarios:

- Auth register/login flow
- Score rolling logic (only latest 5 retained)
- Admin draw execution flow
- Stripe webhook lifecycle sync (checkout completed and subscription update)
- OpenAPI contract file path coverage and `/v3/api-docs` accessibility

Frontend component tests are available and run with:

- `cd frontend`
- `npm run test`

Covered frontend modules:

- Score list rendering
- Draw results card rendering
- Subscription activation UI action

## CI Automation

GitHub Actions workflows are included for both stacks:

- `.github/workflows/backend-ci.yml`
   - Triggers on backend and OpenAPI contract changes
   - Runs backend tests and package verification
- `.github/workflows/frontend-ci.yml`
   - Triggers on frontend changes
   - Runs frontend tests and production build

Unified merge gate workflow:

- `.github/workflows/ci.yml`
   - Runs backend and frontend jobs in one workflow
   - Publishes a final `Required CI Gate` job
   - Use `Required CI Gate` as the required status check in branch protection rules

These workflows are designed to run on every push and pull request, enforcing test and build quality gates before merge.

## Default Seed Data

On first startup, backend seeds:
- Admin user from `ADMIN_EMAIL` and `ADMIN_PASSWORD`
- Two sample charities

## Key Backend Endpoints

- Auth: `/auth/register`, `/auth/login`
- Scores: `/scores` (POST, GET, PUT)
- Subscription: `/subscriptions/me`, `/subscriptions/activate`, `/subscriptions/checkout-session`
- Charity: `/charities`, `/charities/select`
- Draw: `/draws/latest`, `/draws/my-results`, `/draws/admin/execute`
- Dashboard: `/dashboard/me`
- Admin: `/admin/users`, `/admin/winners/{winnerId}/verify`, `/admin/winners/{winnerId}/pay`

## Notes

- Score logic enforces latest 5 scores only and automatically removes oldest entries.
- JWT auth is stateless with role-based route protection.
- Flyway migrations run automatically from `backend/src/main/resources/db/migration`.
- Stripe checkout session and webhook endpoint are implemented for subscription lifecycle sync.
- CORS enabled for frontend-backend integration.
