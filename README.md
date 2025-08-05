# 🧾 Task Management API

This is a simple **Spring Boot REST API** for managing tasks in a case management context. It allows you to:

- ✅ Create a task
- ✅ Retrieve a task by ID
- ✅ Retrieve all tasks
- ✅ Update a task’s status
- ✅ Delete a task

---

## 🚀 Features

### 📘 CRUD Operations
Each task contains:
- `id` – Auto-generated identifier
- `caseNumber` – Required case reference
- `title` – Required title of the task
- `description` – Optional task details
- `status` – Required task status (e.g., `Open`, `In Progress`, `Done`)
- `createdDate` – Auto-generated timestamp when created

---

### 🗃️ Database Integration
- Uses **Spring Data JPA**
- **H2 in-memory database** for development and testing

---

### 🛡️ Validation
- Validates input using `jakarta.validation`
- Required fields: `caseNumber`, `title`, and `status`

---

### ❗ Error Handling
- Global exception handler for:
  - Validation errors
  - General exceptions

---

### ✅ Testing
- **Unit and integration tests** written with JUnit 5 and MockMvc
- Covers all controller endpoints:
  - Success and failure scenarios
  - Status code checks
  - Response body validation

---

### 📖 API Documentation
- Integrated with **SpringDoc OpenAPI**
- Swagger UI available at: