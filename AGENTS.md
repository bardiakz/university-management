# Repository Guidelines

## Project Structure & Module Organization
- `services/` contains Spring Boot microservices, one per bounded context (e.g., `auth-service`, `booking-service`, `exam-service`). Source lives under `src/main/java/...`, tests under `src/test/java/...`.
- `frontend/um_flutter/` is the Flutter client app. UI screens in `lib/screens/`, providers in `lib/providers/`, models in `lib/models/`.
- `docker-compose.yml` defines the full local stack (PostgreSQL, RabbitMQ, Redis, and all services). Database bootstrap is in `init-db.sql`.
- `scripts/` includes utility scripts like `seed_tracking_data.py`.

## Build, Test, and Development Commands
- `docker compose up --build`: Bring up the full microservices stack locally.
- `./gradlew bootRun` (inside `services/<service-name>`): Run a single service locally.
- `./gradlew test` (inside `services/<service-name>`): Run that service’s JUnit test suite.
- `flutter pub get` (in `frontend/um_flutter`): Install Flutter dependencies.
- `flutter run` (in `frontend/um_flutter`): Run the mobile/web client.
- `flutter test` (in `frontend/um_flutter`): Run Flutter tests.

## Coding Style & Naming Conventions
- Java services follow standard Spring Boot conventions: 4-space indentation, `UpperCamelCase` classes, `lowerCamelCase` methods, and lowercase package names (e.g., `io.github.bardiakz.booking_service`).
- Flutter/Dart follows `analysis_options.yaml` and `flutter_lints`. Use `dart format .` before committing UI changes.
- Keep file and directory names descriptive and aligned with feature scope (e.g., `booking_service`, `tracking_service`).

## Testing Guidelines
- Java tests live in `services/*/src/test/java` and typically follow `*ApplicationTests.java` naming; add focused tests near the component under test.
- Flutter tests live in `frontend/um_flutter/test/` (e.g., `widget_test.dart`).
- Run relevant unit tests for each service touched before opening a PR.

## Commit & Pull Request Guidelines
- Git history shows short, lowercase messages with optional prefixes like `fix:`, `add:`, `init:` (e.g., `fix: exam-service`). Keep subject lines concise and scoped.
- PRs should include a clear summary, list of impacted services, and any required config/env changes. Add screenshots for UI changes in `frontend/um_flutter`.

## Configuration & Secrets
- The stack relies on env vars such as `JWT_SECRET`, `INTERNAL_API_SECRET`, and email settings (`MAIL_HOST`, `MAIL_USERNAME`, `MAIL_PASSWORD`). Avoid committing real secrets; use local environment overrides.
