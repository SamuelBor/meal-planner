# Personal Meal Planner

## Project Structure

This is a multi-module Maven project following Domain-Driven Design (DDD) principles.

- **core**: Business logic, Domain Entities (`Meal`, `WeeklyPlan`), and Repositories.
- **api**: REST Controllers, DTOs, Security configuration, and the main Spring Boot application.
- **frontend**: Angular frontend (integrated via `frontend-maven-plugin`).

## Prerequisites

- Java 25
- Maven 3.9+
- Docker (for PostgreSQL)
- Node.js & Angular CLI (for frontend development)

## Getting Started

### 1. Start the Database
Use Docker Compose to spin up the PostgreSQL instance.
```bash
docker-compose up -d
```

### 2. Build the Project
This will build the Java modules and install/build the frontend.
```bash
mvn clean install
```

### 3. Run the Application
Run the main class in the `api` module:
`org.sjb.personal.mealplanning.MealPlanningApplication`

### 4. Frontend Development
The frontend is located in the `frontend` module.
```bash
cd frontend
npm install
npm start
```

## Features

- **Virtual Threads**: Enabled for high-throughput concurrency.
- **Declarative HTTP Client**: Tesco integration uses Spring 6+ `@HttpExchange`.
- **Allergy Guard**: Domain logic to protect specific users from allergens.
- **Smart Scheduling**: Entities prepared for portion and leftover management.
