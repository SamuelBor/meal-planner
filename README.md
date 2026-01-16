# Personal Meal Planner

The Personal Meal Planner is a smart application designed to simplify weekly meal scheduling for households. It takes into account individual attendance, dietary preferences, weather conditions, and freezer inventory to generate an optimized meal plan. By intelligently managing leftovers and prioritizing existing ingredients, it helps reduce food waste and the mental load of deciding "what's for dinner."

## Project Structure

This is a multi-module Maven project following Domain-Driven Design (DDD) principles.

- **core**: Business logic, Domain Entities (`Meal`, `FreezerItem`, `Ingredient`), and Repositories.
- **api**: REST Controllers, DTOs, Security configuration, Swagger/OpenAPI docs, and the main Spring Boot application.
- **infrastructure**: Configuration and external integrations.
- **frontend**: Angular frontend (integrated via `frontend-maven-plugin`).

## Prerequisites
- Java 25
- Maven 3.9+
- Docker (for PostgreSQL)
- Node.js (v20.11.1) & Angular CLI (for frontend development)

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

Access the application at:
- **Frontend**: http://localhost:4200 (if running via `npm start`) or http://localhost:8080 (if served by Spring Boot)
- **Swagger UI**: http://localhost:8080/swagger-ui.html

### 4. Frontend Development
The frontend is located in the `frontend` module.
```bash
cd frontend
npm install
npm start
```
This will start the Angular dev server on port 4200, proxying API requests to the backend on port 8080.

## Features

- **Smart Meal Generation**: Generates a weekly plan (Sunday-Saturday) based on who is eating each day.
- **Leftover Management**: Automatically schedules leftovers for subsequent days if a meal produces extra portions.
- **Freezer Integration**: Tracks freezer inventory (ingredients or full meals) and prioritizes using them to reduce waste.
- **Weather Awareness**: Suggests meals suitable for the predicted weekly weather (Hot, Neutral, Cold).
- **Treat Night**: Randomly schedules a "Meal Out" or "Takeaway" on Friday or Saturday (33% chance) if everyone is home.
- **Shopping List**: Automatically aggregates ingredients from the generated plan into a consolidated shopping list.
- **History**: View previously generated meal plans saved to your local documents folder.
- **Virtual Threads**: Enabled for high-throughput concurrency.
