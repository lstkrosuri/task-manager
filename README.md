# Task Manager — Java Full Stack Application

A full-stack Task Manager application built with **Java, Spring Boot, Angular, and Oracle Database**.

The application allows users to create, view, update, complete, search, filter, sort, and delete tasks through a responsive web interface. It demonstrates RESTful API development, database persistence, frontend/backend integration, form handling, and CRUD operations.

## Tech Stack

| Layer | Technologies |
|---|---|
| Frontend | Angular 21, TypeScript, HTML5, CSS3 |
| Backend | Java, Spring Boot 4, Spring Web |
| Database | Oracle Database |
| Persistence | Spring Data JPA, Hibernate |
| Validation | Jakarta Bean Validation |
| Testing | JUnit 5, Mockito |
| Build Tools | Maven, Angular CLI, npm |
| Development Tools | VS Code, Git, GitHub, Docker |

## Features

### Task Management
- Create tasks with a title and optional description
- View all tasks retrieved from Oracle Database
- Edit existing task titles and descriptions
- Mark tasks as completed or pending
- Delete tasks with confirmation
- Refresh the task list without reloading the page

### Task Organization
- Search tasks by title or description
- Filter tasks by completion status
- Sort tasks by ID or title
- Client-side pagination
- Dashboard statistics for total, completed, and pending tasks

### Backend Features
- RESTful CRUD API
- Input validation for task titles and descriptions
- Centralized exception handling
- HTTP status codes for successful and failed requests
- Database persistence using Spring Data JPA
- Optional backend filtering, sorting, and pagination
- Unit tests using JUnit 5 and Mockito

## Application Architecture

```text
              Angular Frontend
              localhost:4200
                     |
                     | HTTP Requests
                     v
              Spring Boot API
              localhost:8080
                     |
                     | Spring Data JPA
                     v
              Oracle Database
              localhost:1521
```

The Angular frontend communicates with Spring Boot through REST API endpoints. Spring Boot processes requests, performs validation, and uses Spring Data JPA to read and write data in Oracle Database.

During local development, Angular uses a proxy to forward requests beginning with `/api` to the Spring Boot server.

## Project Structure

```text
taskmanager/
|
|-- src/
|   |-- main/
|   |   |-- java/com/lstkrosuri/taskmanager/
|   |   |   |-- controller/
|   |   |   |   `-- TaskController.java
|   |   |   |-- service/
|   |   |   |   `-- TaskService.java
|   |   |   |-- repository/
|   |   |   |   `-- TaskRepository.java
|   |   |   |-- model/
|   |   |   |   `-- Task.java
|   |   |   |-- exception/
|   |   |   |   `-- GlobalExceptionHandler.java
|   |   |   `-- TaskmanagerApplication.java
|   |   `-- resources/
|   |       `-- application.properties
|   `-- test/
|       `-- java/
|
|-- taskmanager-frontend/
|   |-- src/
|   |   |-- app/
|   |   |   |-- models/
|   |   |   |   `-- task.ts
|   |   |   |-- services/
|   |   |   |   `-- task.ts
|   |   |   |-- app.ts
|   |   |   |-- app.html
|   |   |   |-- app.css
|   |   |   `-- app.config.ts
|   |   `-- styles.css
|   |-- proxy.conf.json
|   |-- angular.json
|   `-- package.json
|
|-- mvnw
|-- pom.xml
`-- README.md
```

## REST API Endpoints

Base URL: `http://localhost:8080`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/tasks` | Retrieve all tasks |
| GET | `/api/tasks/{id}` | Retrieve a task by ID |
| POST | `/api/tasks` | Create a new task |
| PUT | `/api/tasks/{id}` | Update an existing task |
| DELETE | `/api/tasks/{id}` | Delete a task |

### Additional Query Parameters

The GET `/api/tasks` endpoint also supports:

| Parameter | Example | Description |
|---|---|---|
| `completed` | `true` | Filter by completion status |
| `sort` | `title` | Sort by `id` or `title` |
| `page` | `0` | Zero-based page number |
| `size` | `10` | Number of tasks per page |

Example:

```http
GET /api/tasks?completed=false&page=0&size=10&sort=title
```

When pagination parameters are provided, the API returns a paginated response. Without them, it returns a JSON array of tasks.

## Task Data Model

Each task contains the following properties:

| Field | Type | Description |
|---|---|---|
| `id` | Long | Unique database-generated ID |
| `title` | String | Required task title, maximum 200 characters |
| `description` | String | Optional description, maximum 1000 characters |
| `completed` | Boolean | Task completion status |

Example JSON response:

```json
{
  "id": 81,
  "title": "Learn Angular HTTP POST",
  "description": "Create tasks using Angular and Spring Boot",
  "completed": false
}
```

## Getting Started

### Prerequisites

Install or configure:

- Java 21 or later
- Node.js and npm compatible with Angular 21
- Angular CLI
- Docker Desktop
- Oracle Database
- Git

This project was developed locally using Java, Maven, Angular 21, and an Oracle Database container.

### 1. Clone the Repository

```bash
git clone https://github.com/lstkrosuri/task-manager.git
cd task-manager
```

### 2. Start Oracle Database

The local development environment uses an Oracle Database container named `oracle-taskmanager`.

If the container has already been created:

```bash
docker start oracle-taskmanager
```

Check its status:

```bash
docker ps
```

The database configuration expects:

```text
Host: localhost
Port: 1521
Service: FREEPDB1
Database User: TASKMANAGER
```

**Important:** The Oracle container, database user, and required permissions must already be configured. Cloning the repository does not automatically create the database container or database account.

### 3. Configure the Backend

Open:

```text
src/main/resources/application.properties
```

The local configuration uses environment-based password injection:

```properties
spring.application.name=taskmanager

spring.datasource.url=jdbc:oracle:thin:@//localhost:1521/FREEPDB1
spring.datasource.username=TASKMANAGER
spring.datasource.password=${DB_PASSWORD}
spring.datasource.driver-class-name=oracle.jdbc.OracleDriver

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

Set the database password in your terminal.

On macOS with zsh:

```bash
read -s "DB_PASSWORD?Enter database password: "
echo
export DB_PASSWORD
```

Never commit database passwords or other secrets to GitHub.

### 4. Start the Spring Boot Backend

From the project root:

```bash
./mvnw spring-boot:run
```

The backend starts at:

```text
http://localhost:8080
```

Verify the API:

```bash
curl -i http://localhost:8080/api/tasks
```

A successful request returns HTTP `200` and a JSON response.

### 5. Install Angular Dependencies

Open another terminal:

```bash
cd taskmanager-frontend
npm install
```

### 6. Start the Angular Frontend

Run:

```bash
ng serve --proxy-config proxy.conf.json --open
```

The frontend runs at:

```text
http://localhost:4200
```

Angular forwards API requests to the Spring Boot backend through the development proxy.

The proxy configuration is:

```json
{
  "/api/**": {
    "target": "http://localhost:8080",
    "secure": false,
    "changeOrigin": true
  }
}
```

## Example API Requests

### Get All Tasks

```bash
curl http://localhost:8080/api/tasks
```

### Get a Task by ID

```bash
curl http://localhost:8080/api/tasks/81
```

### Create a Task

```bash
curl -X POST http://localhost:8080/api/tasks \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Complete Task Manager Project",
    "description": "Finish the Java full-stack application",
    "completed": false
  }'
```

### Update a Task

Replace `81` with an existing task ID:

```bash
curl -X PUT http://localhost:8080/api/tasks/81 \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Learn Angular HTTP POST",
    "description": "Create tasks using Angular and Spring Boot",
    "completed": true
  }'
```

The update endpoint expects the task title, description, and completion status.

### Delete a Task

Replace `81` with the ID of a task you want to remove:

```bash
curl -X DELETE http://localhost:8080/api/tasks/81
```

**Warning:** Deleting a task removes its database record.

## Input Validation and Error Handling

The backend validates incoming task data using Jakarta Bean Validation.

Validation rules include:

- Task title cannot be blank.
- Task title cannot exceed 200 characters.
- Task description cannot exceed 1000 characters.
- Requests for nonexistent tasks return HTTP `404`.
- Invalid input returns HTTP `400`.
- Successful task creation returns HTTP `201`.
- Successful deletion returns HTTP `204`.

The application uses a centralized exception handler to provide structured error responses.

## Testing

The backend includes unit tests for task operations and validation behavior.

Run the test suite from the project root:

```bash
./mvnw test
```

The existing test suite includes tests for:

- Retrieving an existing task
- Handling nonexistent tasks
- Creating tasks
- Updating tasks
- Deleting tasks
- Pagination
- Filtering
- Invalid pagination parameters
- Invalid sorting fields

The project also includes a Spring Boot application context test that requires the configured Oracle database to be available.

## Production Build

To create a production build of the Angular application:

```bash
cd taskmanager-frontend
npx ng build
```

The generated files are placed under:

```text
taskmanager-frontend/dist/taskmanager-frontend/
```

The Angular application is configured for a browser-based static build.

## Deployment

The application is currently configured for local development.

Potential deployment platforms include:

- **Angular frontend:** Cloudflare Pages, Netlify, or Vercel
- **Spring Boot backend:** Oracle Cloud Infrastructure, Render, or another Java-compatible host
- **Oracle database:** Oracle Cloud Infrastructure / Oracle Autonomous Database

Before deploying, update the application for the production environment:

1. Configure a publicly accessible HTTPS backend.
2. Configure the frontend's production API routing.
3. Move database credentials into environment variables or a secrets manager.
4. Configure secure database connectivity.
5. Restrict CORS and network access appropriately.

The Angular development proxy does not automatically apply to a production deployment.

## Future Enhancements

Possible improvements include:

- User authentication and authorization using Spring Security
- User-specific task lists
- Task priorities and due dates
- Task categories and labels
- Server-side search and pagination
- Automated frontend tests
- CI/CD pipelines using GitHub Actions
- Cloud deployment with HTTPS
- Docker-based deployment configuration

## Learning Outcomes

This project demonstrates practical experience with:

- Building RESTful APIs using Spring Boot
- Implementing CRUD operations
- Integrating Oracle Database with Spring Data JPA
- Designing Angular standalone components
- Using Angular services and HttpClient
- Handling forms and two-way data binding
- Managing asynchronous HTTP requests
- Implementing frontend search, filtering, and pagination
- Writing backend unit tests
- Using Git and GitHub for version control

## Repository

GitHub: https://github.com/lstkrosuri/task-manager

## Project Status

**Local development:** Implemented

**Angular production build:** Successful

**Cloud deployment:** Planned

---

Built as a hands-on Java Full Stack development project using Angular, Spring Boot, and Oracle Database.