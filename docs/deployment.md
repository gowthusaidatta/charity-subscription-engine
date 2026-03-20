# Deployment Guide (Vercel + Render + PostgreSQL)

## 1. Database (Supabase PostgreSQL)

1. Create a new Supabase project.
2. From Project Settings > Database, copy:
   - Host
   - Port
   - Database name
   - Username
   - Password
3. Build JDBC URL:
   - `jdbc:postgresql://<host>:5432/postgres?sslmode=require`

## 2. Backend Deployment (Render)

1. Push repository to GitHub.
2. In Render, create a new Web Service from the repo.
3. Root directory: `backend`
4. Build command:
   - `mvn clean package -DskipTests`
5. Start command:
   - `java -jar target/golf-charity-backend-1.0.0.jar`
6. Set environment variables:
   - `PORT=10000` (Render injects port, but app supports env override)
   - `DB_URL=jdbc:postgresql://<host>:5432/postgres?sslmode=require`
   - `DB_USERNAME=<db_user>`
   - `DB_PASSWORD=<db_password>`
   - `JWT_SECRET=<very-strong-min-64-char-secret>`
   - `JWT_EXPIRATION_MS=900000`
   - `STRIPE_SECRET_KEY=<stripe_secret_key>`
   - `STRIPE_MONTHLY_PRICE_ID=<stripe_monthly_price_id>`
   - `STRIPE_YEARLY_PRICE_ID=<stripe_yearly_price_id>`
   - `STRIPE_WEBHOOK_SECRET=<stripe_webhook_signing_secret>`
   - `MONTHLY_FEE=49.00`
   - `YEARLY_FEE=499.00`
   - `PRIZE_POOL_PERCENT=40.00`
   - `PRIZE_TIER5_PERCENT=40.00`
   - `PRIZE_TIER4_PERCENT=35.00`
   - `PRIZE_TIER3_PERCENT=25.00`
   - `DRAW_MONTHLY_CRON=0 0 1 1 * *`
   - `DRAW_MONTHLY_MODE=RANDOM`
   - `EMAIL_NOTIFICATIONS_ENABLED=true`
   - `MAIL_FROM=<verified_sender_email>`
   - `MAIL_HOST=<smtp_host>`
   - `MAIL_PORT=587`
   - `MAIL_USERNAME=<smtp_user>`
   - `MAIL_PASSWORD=<smtp_password>`
   - `MAIL_SMTP_AUTH=true`
   - `MAIL_STARTTLS_ENABLE=true`
   - `ADMIN_EMAIL=<admin_email>`
   - `ADMIN_PASSWORD=<admin_password>`
7. Deploy and verify:
   - Health URL: `https://<render-app>/actuator/health`
   - Swagger URL: `https://<render-app>/swagger-ui`

## 3. Frontend Deployment (Vercel)

1. In Vercel, import the same repository.
2. Set project root to `frontend`.
3. Build command:
   - `npm run build`
4. Output directory:
   - `dist`
5. Set environment variables:
   - `VITE_API_URL=https://<render-app>/api/v1`
6. Deploy and verify routes:
   - `/`
   - `/login`
   - `/signup`
   - `/dashboard`
   - `/admin`

## 4. JWT Secret and Security

- Use long random secret (64+ chars).
- Rotate secrets periodically.
- Keep all credentials in platform env vars, never in source code.

## 5. Production Best Practices

1. Enable HTTPS only and strict transport security.
2. Use managed PostgreSQL with backups and point-in-time recovery.
3. Configure Render health checks and auto-restart.
4. Add log aggregation (Render logs + external APM).
5. Add API rate limiting for auth and admin routes.
6. Restrict CORS origins to exact frontend domains in production.
7. Add CI pipeline with build and test gates.
8. Use rolling deploy strategy for zero downtime.
9. Use Stripe webhook signature verification before applying subscription state changes.
10. Configure Stripe webhook endpoint to: `https://<render-app>/api/v1/subscriptions/stripe/webhook`.
11. Verify scheduler timezone/cron behavior for monthly draw execution in production.

## 6. Public Accessibility Checklist

- Backend is reachable over HTTPS on Render
- Frontend is reachable over HTTPS on Vercel
- Frontend can call backend API URL
- Signup/login works
- Dashboard and admin routes work with JWT tokens
- PostgreSQL connectivity stable in production
