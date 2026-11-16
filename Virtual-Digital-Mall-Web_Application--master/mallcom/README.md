# Mallcom – Mall Management Web Application
## Complete Setup Guide

---

## Project Structure

```
mallcom/
├── backend/                          ← Spring Boot project
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/mallcom/
│       │   ├── MallcomApplication.java
│       │   ├── controller/
│       │   │   └── AuthController.java
│       │   ├── entity/
│       │   │   └── User.java
│       │   ├── repository/
│       │   │   └── UserRepository.java
│       │   └── service/
│       │       └── UserService.java
│       └── resources/
│           └── application.properties
│
└── frontend/                         ← All HTML/CSS/JS files
    ├── Minor_project.html            ← Home (UPDATED navbar)
    ├── Minor_project.css
    ├── brands.html                   ← (UPDATED navbar)
    ├── brands.css
    ├── luxery.html                   ← (UPDATED navbar)
    ├── luxery.css
    ├── luxery.js
    ├── search.html                   ← (UPDATED navbar)
    ├── login.html                    ← (UPDATED – new auth logic)
    ├── register.html                 ← (UPDATED – new form + auth logic)
    ├── Contact.html
    ├── contact.css
    ├── Dining.html
    ├── Explore_Brands.html           ← Replaces index.html (Featured Stores)
    ├── food_delight.html
    ├── food_delight.css
    └── aboutus.html
```

---

## Files Deleted / Disconnected

| File | Reason |
|------|--------|
| `index.html` | Was the Fashion Recommendation System launcher (Streamlit link). Replaced by `Explore_Brands.html` in navigation. |

---

## Files Modified

| File | What Changed |
|------|-------------|
| `register.html` | Removed username/phone fields. Form now sends only `fullName`, `email`, `password`. Removed JWT token storage. Redirects to login after success. |
| `login.html` | Changed input from `emailOrUsername` to `email` only. Removed JWT storage. Stores `{userId, fullName, email}` in localStorage. |
| `Minor_project.html` | Navbar: "Recommend → index.html" replaced with "Featured Stores → Explore_Brands.html". Added Login/Logout link with user name display. |
| `brands.html` | Navbar: same Recommend → Featured Stores change. |
| `luxery.html` | Navbar: same change. |
| `search.html` | Navbar: same change. |

---

## MySQL Setup

```sql
-- Run in MySQL Workbench or terminal
CREATE DATABASE mallcom;
USE mallcom;

-- Table is auto-created by Spring Boot (ddl-auto=update)
-- But you can create it manually:
CREATE TABLE users (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email    VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);
```

---

## Backend Setup (Spring Boot)

### Prerequisites
- Java 17+
- Maven 3.6+
- MySQL 8.0+

### Steps

```bash
# 1. Navigate to backend folder
cd mallcom/backend

# 2. Start MySQL and create database
mysql -u root -p
> CREATE DATABASE mallcom;
> EXIT;

# 3. Update credentials if needed
# Edit: src/main/resources/application.properties
# Change spring.datasource.username and spring.datasource.password

# 4. Build and run
mvn spring-boot:run

# Server starts at http://localhost:8080
```

---

## API Reference

### POST /api/auth/register

**Request:**
```json
{
  "fullName": "John Doe",
  "email": "john@gmail.com",
  "password": "123456"
}
```

**Success (200):**
```json
{
  "message": "Registration Successful",
  "success": true
}
```

**Failure (400):**
```json
{
  "message": "Email already exists",
  "success": false
}
```

---

### POST /api/auth/login

**Request:**
```json
{
  "email": "john@gmail.com",
  "password": "123456"
}
```

**Success (200):**
```json
{
  "message": "Login Successful",
  "userId": 1,
  "fullName": "John Doe",
  "success": true
}
```

**Failure (401):**
```json
{
  "message": "Invalid Email or Password",
  "success": false
}
```

---

## Frontend Setup

No build step needed. Just open files in a browser or serve them with any static server.

```bash
# Option A: Open directly
open frontend/Minor_project.html

# Option B: Use VS Code Live Server extension

# Option C: Python simple server
cd mallcom/frontend
python -m http.server 5500
# Visit http://localhost:5500/Minor_project.html
```

**Important:** Backend must be running on port 8080 before using login/register.

---

## Logout

Logout is handled client-side. On the Home page navbar:
- If logged in: shows "Logout (FirstName)" — clicking clears localStorage and redirects to login.html
- If not logged in: shows "Login" link.

---

## What Was Removed

- Fashion Recommendation System (index.html Streamlit launcher)
- JWT token logic from register/login pages
- `username`, `phone`, `role`, `enabled`, `createdAt`, `updatedAt` fields from User
- All references to AI Recommendation, ResNet50, embeddings

## What Was Preserved

- All page designs (Home, Brands, Luxury, Search, Dining, Contact, About Us)
- All CSS files untouched
- All slider/JS functionality
- All existing navigation (only the Recommend link was replaced)
