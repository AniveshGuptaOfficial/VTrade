# VTrade Backend (Spring Boot)

## Run
```
cd backend
mvn spring-boot:run
```
Server starts on **http://localhost:8080**.

## Database
H2 in-memory DB. Console at http://localhost:8080/h2-console
(JDBC URL: `jdbc:h2:mem:vtrade`, user `sa`, blank password).

## Key Endpoints
- `GET  /api/health` — health check
- `POST /api/auth/register` — email/password signup
- `POST /api/auth/login` — email/password login
- `POST /api/auth/login/otp/send` — send OTP (returns `otp` in demo mode)
- `POST /api/auth/login/otp/verify` — verify OTP, returns JWT
- `GET  /api/products?category=&search=` — browse products (public)
- `POST /api/products` — add product (auth required)
- `GET  /api/marketplace?category=` — browse listings (public)
- `POST /api/marketplace` — create listing (auth)
- `PATCH /api/marketplace/{id}/sold` — mark sold (auth)
- `POST /api/pickup` — create pickup request (auth)
- `GET  /api/pickup/mine` — my pickup requests (auth)
- `GET  /api/pickup/open` — open requests for workers (auth)
- `PATCH /api/pickup/{id}/accept` — worker accepts (auth)
- `PATCH /api/pickup/{id}/status` — update status (auth)
- `POST /api/orders` — place order (auth)
- `GET  /api/orders/mine` — order history (auth)
- `GET  /api/users/me` — profile (auth)
- `PUT  /api/users/me` — update profile (auth)
- `POST /api/users/me/become-worker` — opt into worker program (auth)
- `PATCH /api/users/me/online` — toggle worker online status (auth)
- `GET/POST/DELETE /api/users/me/slots` — worker availability slots (auth)
- `GET  /api/chat?contextType=&contextId=` — chat history
- `POST /api/chat` — send chat message (auth)

## Auth
Send `Authorization: Bearer <token>` header. Token returned from register/login/OTP-verify.

## Notes
- `vtrade.otp.demo-mode=true` in `application.properties` returns the OTP code in the
  `/auth/login/otp/send` response for easy testing without an SMS gateway.
- 12 sample products are auto-seeded into H2 on first run.
