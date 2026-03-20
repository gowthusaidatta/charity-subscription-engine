# Golf Charity Subscription Platform - System Spec

## 1. Full System Architecture

### Core Components

- React frontend (Vite + Tailwind + Axios)
- Spring Boot backend API (JWT secured)
- PostgreSQL database (JPA/Hibernate)
- Deployment targets:
  - Frontend on Vercel
  - Backend on Render
  - Database on Supabase PostgreSQL

### Runtime Flow

1. Client authenticates with `/api/v1/auth/login` or `/api/v1/auth/register`
2. Backend issues JWT
3. Frontend stores JWT and sends `Authorization: Bearer <token>` on protected calls
4. `JwtAuthenticationFilter` validates token and sets security context
5. Controllers call service layer, services call repositories (clean architecture)
6. Database persists users, subscriptions, scores, draws, winners, charities

## 2. Database Schema (Tables + Relations)

### users
- id (UUID, PK)
- full_name
- email (unique)
- password_hash
- role (USER, ADMIN)
- selected_charity_id (FK -> charities.id)
- charity_contribution_percent
- created_at
- updated_at

### subscriptions
- id (UUID, PK)
- user_id (UUID, FK -> users.id, unique one-to-one)
- plan (MONTHLY, YEARLY)
- status (ACTIVE, INACTIVE, CANCELED, LAPSED)
- renewal_date
- created_at
- updated_at

### scores
- id (UUID, PK)
- user_id (UUID, FK -> users.id)
- score_value (1..45)
- score_date
- created_at
- updated_at

### charities
- id (UUID, PK)
- name
- slug
- description
- image_url
- active
- featured
- created_at
- updated_at

### draws
- id (UUID, PK)
- month_key (YYYY-MM, unique)
- draw_date
- mode (RANDOM, WEIGHTED)
- winning_numbers (CSV in DB)
- published
- created_at

### winners
- id (UUID, PK)
- draw_id (UUID, FK -> draws.id)
- user_id (UUID, FK -> users.id)
- match_count (3,4,5)
- prize_amount
- verification_status (PENDING, APPROVED, REJECTED)
- payout_status (PENDING, PAID)
- proof_url
- created_at

## 3. REST API Endpoints

## Auth
- `POST /api/v1/auth/register`
- `POST /api/v1/auth/login`

## Scores
- `POST /api/v1/scores`
- `GET /api/v1/scores`
- `PUT /api/v1/scores/{scoreId}`

## Subscription
- `GET /api/v1/subscriptions/me`
- `POST /api/v1/subscriptions/activate`
- `PUT /api/v1/subscriptions/admin/{userId}` (admin)

## Charity
- `GET /api/v1/charities`
- `POST /api/v1/charities/select`
- `POST /api/v1/charities/admin` (admin)
- `PUT /api/v1/charities/admin/{charityId}` (admin)
- `DELETE /api/v1/charities/admin/{charityId}` (admin)

## Draw
- `GET /api/v1/draws/latest`
- `GET /api/v1/draws/my-results`
- `POST /api/v1/draws/admin/execute` (admin)
- `GET /api/v1/draws/admin/{drawId}/winners` (admin)
- Monthly scheduled execution supported via cron (`app.draw.monthly-cron`)

## Dashboard
- `GET /api/v1/dashboard/me`

## Admin Operations
- `GET /api/v1/admin/users`
- `GET /api/v1/admin/analytics`
- `PUT /api/v1/admin/users/{userId}/subscription`
- `PUT /api/v1/admin/winners/{winnerId}/verify`
- `PUT /api/v1/admin/winners/{winnerId}/pay`

## Winner Proof
- `POST /api/v1/winners/{winnerId}/proof`

## 4. Folder Structure

```text
backend/
  src/main/java/com/digitalheroes/golfcharity/
    admin/
    auth/
    charity/
    common/
    config/
    dashboard/
    draw/
    enums/
    score/
    security/
    subscription/
    user/
    winner/
  src/main/resources/application.yml
  Dockerfile
  pom.xml

frontend/
  src/
    components/
    context/
    pages/
    services/
    App.jsx
    main.jsx
    index.css
  package.json
  tailwind.config.js
  vite.config.js
  vercel.json

docs/
  deployment.md
  system-spec.md
```

## 5. Application Flow

### User Flow

1. Register/Login
2. Activate subscription (monthly/yearly)
3. Select charity with minimum 10% contribution
4. Enter Stableford scores
5. System always keeps latest 5 scores and deletes oldest automatically
6. User sees dashboard status, score history, and draw outcomes

### Admin Flow

1. Login as admin
2. View users and profile status
3. Execute monthly draw with RANDOM or WEIGHTED mode
4. View winners by draw
5. Verify winner proofs and mark payouts as paid
6. Manage charities (create/edit/delete)

### Draw Execution Logic

- Eligible users: users with ACTIVE subscription
- Entry source: latest 5 user scores
- Modes:
  - RANDOM: random unique 5 numbers from 1 to 45
  - WEIGHTED: weighted by global score frequency
- Match levels: 3, 4, 5
- Tier pool distribution:
  - 5 match: 40%
  - 4 match: 35%
  - 3 match: 25%
- Winners within each tier split pool equally
- Unclaimed 5-match pool rolls over to next published month
- Winners stored with status tracking for verification and payment
