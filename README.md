# ByteBharat Digital Banking

ByteBharat is a full-stack banking learning application with a Java Spring Boot REST API and a React + Vite frontend. Account balances and activity are maintained by the application; no financial institution or payment network is connected, and transactions do not move real funds.

## What is included

- Overview, CASA accounts, fixed deposits, loan applications, payments, cards, statements, profile maintenance, and spending insights
- API-backed account, deposit, and loan details and statements
- In-memory account ledger with transfer, card, and payment operations
- Dashboard personalization, light and dark themes, and responsive layouts
- Password reset by email through SMTP

## Run on Windows

Requirements: Java 21, Maven 3.6.3+ (or Eclipse with Maven integration), Node.js 20.19+ or 22.12+, and npm.

### Start the API

Open PowerShell in `backend` and run:

    mvn spring-boot:run

The API runs at `http://localhost:8080`.

### Start the frontend

Open another PowerShell window in `frontend` and run:

    npm install
    npm run dev

Open the Vite URL, usually `http://localhost:5173`. Vite forwards `/api` requests to port 8080.

## Sign in

The initial account is `customer` with password `Banking@12345`. Passwords are stored as BCrypt hashes. Five consecutive invalid attempts lock the account for 15 minutes. Change the initial password before sharing a deployment.

JWTs expire after 30 minutes. The signing key is generated at startup, so sign in again after a backend restart. The browser keeps the token in memory only.

## Password reset email

Configure `MAIL_HOST`, `MAIL_PORT`, `MAIL_USERNAME`, `MAIL_PASSWORD`, and `MAIL_FROM` in the backend environment. Gmail defaults use SMTP on port 587 with STARTTLS. Set `MAIL_PASSWORD` to a Google App Password in the active PowerShell session; do not commit mail credentials. If email delivery fails, the reset request is rejected and the code cannot be used.

## API routes

- `GET /api/health`
- `GET /api/core/dashboard`, `/api/core/accounts`, `/api/core/transactions`, `/api/core/cards`
- `POST /api/core/transfers` and `PATCH /api/core/cards/{cardId}`
- `POST /api/auth/login`, `/api/auth/password-reset/request`, and `/api/auth/password-reset/complete`
- `GET` and `POST /api/modules/casa`, `/api/modules/deposits`, and `/api/modules/loans`
- `POST /api/modules/payments`
- `GET` and `PATCH /api/auth/me` and `/api/auth/profile`

Banking API routes require the bearer token returned by `/api/auth/login`; health and sign-in routes are public.

## Data and boundaries

Account, card, deposit, loan, and payment activity is held in server memory and resets when the backend restarts. User credentials and profile details are stored in the local H2 database. The application has no bank integration, payment rail, or lending decision and must not be used for real funds or customer financial data.

To change the account, transaction, card, and insight values, edit `backend/src/main/java/com/northstar/banking/core/CoreBankingService.java`. Product records are initialized in `DepositService` and `LoanService`. Restart Spring Boot to reload the in-memory ledger.

The application is a modular monolith: controllers delegate to module services and outbound adapters. Payment rails use a Strategy registry through `PaymentProcessor`; account processing is isolated behind `AccountLedgerPort`.

## Frontend customization

Use the **Customize** button to arrange dashboard widgets and change appearance. Navigation and widgets are in `frontend/src/App.jsx`; colors, spacing, and responsive behavior are in `frontend/src/styles.css`.
