# BSPQ26-E5
Repository for team BSPQ26-E5

# JustOrder

A full-stack food ordering application built with Spring Boot and React.

## Tech Stack

| Layer    | Technology                     |
|----------|--------------------------------|
| Backend  | Spring Boot 4, Java 21, Maven  |
| Frontend | React, JavaScript              |
| Database | PostgreSQL                     |

## Getting Started

### Prerequisites

- Java 21+
- Node.js 18+
- Maven 3.9+
- PostgreSQL 14+

### Database Setup

1. Create the database:
   ```bash
   sudo -u postgres createdb justorderdb
   ```

2. Set the postgres user password:
   ```bash
   sudo -u postgres psql -c "ALTER USER postgres PASSWORD 'postgres';"
   ```

3. Verify the connection:
   ```bash
   psql -h localhost -U postgres -d justorderdb
   ```

5. (Optional) Connect to the database directly to debug:
   ```
   sudo -u postgres psql
   \c justorderdb
   ```

### Backend

```bash
cd backend
./mvnw spring-boot:run
```

The API will be available at `http://localhost:8080`

### Frontend

```bash
cd frontend
npm install
npm start
```

The app will be available at `http://localhost:3000`

## Enforce Tests Before Commit and Merge

### 1) Enforce tests before local commit/push

Set up repository-managed Git hooks (one time):

```bash
./scripts/setup-git-hooks.sh
```

After this, every commit and push runs:

- Backend tests: `backend/./mvnw -B test`
- Frontend tests: `frontend/CI=true npm test -- --watchAll=false`

If any test fails, commit/push is blocked.

### 2) Enforce tests before merge (GitHub)

This repo now includes CI at `.github/workflows/ci.yml` with two checks:

- `backend-tests`
- `frontend-tests`

In GitHub: **Settings → Branches → Branch protection rules → main**:

1. Enable **Require a pull request before merging**
2. Enable **Require status checks to pass before merging**
3. Select `backend-tests` and `frontend-tests` as required checks
4. (Recommended) Enable **Restrict who can push to matching branches**

With this setup, PRs cannot be merged unless both test jobs pass.

## Project Structure

### Backend (`/backend`)

| Package      | Purpose                              |
|--------------|--------------------------------------|
| `controller` | REST API endpoints                   |
| `service`    | Business logic                       |
| `repository` | Database access (JPA repositories)   |
| `model`      | Entity classes                       |
| `dto`        | Data Transfer Objects for API I/O    |
| `mapper`     | Entity ↔ DTO conversion              |
| `config`     | Application configuration            |
| `exception`  | Global error handling                |

### Frontend (`/frontend`)

| Folder       | Purpose                              |
|--------------|--------------------------------------|
| `pages`      | Full page views                      |
| `components` | Reusable UI components               |
| `api`        | Backend API calls                    |
| `routes`     | Application routing                  |
| `store`      | Global state management              |
| `hooks`      | Custom React hooks                   |
| `utils`      | Helper functions                     |
| `styles`     | Global styles                        |

## API Endpoints

| Method | Endpoint      | Description         |
|--------|---------------|---------------------|
| GET    | `/api/hello`  | Health check        |

## Team

BSPQ26-E5

## License

See [LICENSE](LICENSE) for details.