# 🏢 Recruitment Management Service

A comprehensive backend REST API application for managing the end-to-end recruitment process, built with **Spring Boot 4**, **Java 21**, **PostgreSQL**, and **Spring Security** with JWT + OTP-based two-factor authentication.

---

## 📋 Table of Contents

- [Overview](#overview)
- [Tech Stack](#tech-stack)
- [Architecture](#architecture)
- [Roles & Permissions](#roles--permissions)
- [Authentication Flow](#authentication-flow)
- [API Endpoints](#api-endpoints)
- [Project Structure](#project-structure)
- [Prerequisites](#prerequisites)
- [Setup & Installation](#setup--installation)
- [Configuration](#configuration)
- [Running the Application](#running-the-application)
- [Default Credentials](#default-credentials)
- [Sample API Requests](#sample-api-requests)
- [Database Schema](#database-schema)
- [Troubleshooting](#troubleshooting)

---

## Overview

The Recruitment Management Service provides a role-based platform where:

- **Super Admins** manage companies and onboard COOs
- **COOs** enlist companies and onboard HRs
- **HRs** post technical and non-technical jobs with skillsets
- **Candidates** create profiles, choose a category (Technical/Non-Technical), browse matching jobs, and apply

All operations are secured via **JWT token-based authentication** with a **two-factor OTP verification** (sent to email and mobile).

---

## Tech Stack

| Technology | Version | Purpose |
|---|---|---|
| Java | 21 | Programming Language |
| Spring Boot | 4.0.5 | Application Framework |
| Spring Security | 6.x | Authentication & Authorization |
| Spring Data JPA | - | ORM / Database Operations |
| PostgreSQL | 14+ | Relational Database |
| JSON Web Token (JWT) | jjwt 0.12.6 | Stateless API Authentication |
| JavaMailSender | - | Email OTP Delivery |
| Twilio SDK | 10.6.4 | SMS OTP Delivery |
| Lombok | - | Boilerplate Reduction |
| Gradle | 9.x | Build Tool |

---

## Architecture

```
┌─────────────────────────────────────────────────────────────────┐
│                        REST Controllers                         │
│  AuthController │ SuperAdminController │ CooController │ ...    │
├─────────────────────────────────────────────────────────────────┤
│                        Service Layer                            │
│  AuthService │ CompanyService │ JobService │ CandidateService   │
│  OtpService  │ EmailService  │ SmsService │ UserService         │
├─────────────────────────────────────────────────────────────────┤
│                     Security Layer                              │
│  JwtUtils │ JwtAuthFilter │ SecurityConfig │ UserDetailsService │
├─────────────────────────────────────────────────────────────────┤
│                      Repository Layer                           │
│  UserRepo │ CompanyRepo │ JobRepo │ CandidateProfileRepo │ ... │
├─────────────────────────────────────────────────────────────────┤
│                    PostgreSQL Database                           │
│  users │ companies │ jobs │ candidate_profiles │ otp_tokens     │
└─────────────────────────────────────────────────────────────────┘
```

---

## Roles & Permissions

| Role | Permissions |
|---|---|
| **SUPER_ADMIN** | Create companies, delete companies, create COO users, view all companies & COOs |
| **COO** | Create companies (cannot delete), onboard/create HR users, view companies & HRs |
| **HR** | Post jobs (technical/non-technical) with skillsets, view posted jobs, view job applications |
| **CANDIDATE** | Register self, create profile (choose TECHNICAL or NON_TECHNICAL category), browse matching jobs, apply to jobs, view applications |

---

## Authentication Flow

The application uses a **two-step authentication** process:

```
Step 1: Login with Email + Password
   POST /api/auth/login
   → Validates credentials
   → Generates 6-digit OTP
   → Sends OTP to registered email AND mobile number
   → Returns confirmation with masked mobile number

Step 2: Verify OTP
   POST /api/auth/verify-otp
   → Validates OTP (5-minute expiry)
   → Issues JWT token (24-hour expiry)
   → Returns Bearer token for API access

Step 3: Access Protected APIs
   Authorization: Bearer <jwt-token>
   → Token is validated on every request
   → Role-based access control enforced
```

---

## API Endpoints

### 🔓 Authentication (Public)

| Method | Endpoint | Description | Request Body |
|---|---|---|---|
| `POST` | `/api/auth/login` | Login with email & password | `{ "email", "password" }` |
| `POST` | `/api/auth/verify-otp` | Verify OTP & get JWT token | `{ "email", "otp" }` |
| `POST` | `/api/auth/register/candidate` | Candidate self-registration | `{ "email", "password", "fullName", "mobileNumber" }` |

### 🔴 Super Admin (`SUPER_ADMIN` role required)

| Method | Endpoint | Description | Request Body |
|---|---|---|---|
| `POST` | `/api/super-admin/companies` | Create a company | `{ "name", "address", "website" }` |
| `DELETE` | `/api/super-admin/companies/{id}` | Delete a company | - |
| `GET` | `/api/super-admin/companies` | List all companies | - |
| `POST` | `/api/super-admin/coo` | Create a COO user | `{ "email", "password", "fullName", "mobileNumber" }` |
| `GET` | `/api/super-admin/coo` | List all COOs | - |

### 🟠 COO (`COO` role required)

| Method | Endpoint | Description | Request Body |
|---|---|---|---|
| `POST` | `/api/coo/companies` | Enlist a company | `{ "name", "address", "website" }` |
| `GET` | `/api/coo/companies` | List all companies | - |
| `POST` | `/api/coo/hr` | Onboard an HR user | `{ "email", "password", "fullName", "mobileNumber", "companyId" }` |
| `GET` | `/api/coo/hr` | List all HR users | - |

### 🟡 HR (`HR` role required)

| Method | Endpoint | Description | Request Body |
|---|---|---|---|
| `POST` | `/api/hr/jobs` | Post a new job | `{ "title", "description", "skillset", "category", "location" }` |
| `GET` | `/api/hr/jobs` | List jobs posted by this HR | - |
| `GET` | `/api/hr/jobs/{jobId}/applications` | View applications for a job | - |

### 🟢 Candidate (`CANDIDATE` role required)

| Method | Endpoint | Description | Request Body |
|---|---|---|---|
| `POST` | `/api/candidate/profile` | Create profile (choose category) | `{ "category", "skills", "resumeUrl", "experienceYears" }` |
| `PUT` | `/api/candidate/profile` | Update profile | `{ "category", "skills", "resumeUrl", "experienceYears" }` |
| `GET` | `/api/candidate/profile` | Get own profile | - |
| `GET` | `/api/candidate/jobs` | Browse jobs matching category | - |
| `POST` | `/api/candidate/jobs/{jobId}/apply` | Apply to a job | - |
| `GET` | `/api/candidate/applications` | View own applications | - |

---

## Project Structure

```
src/main/java/com/miniorange/recruitmentmanagementservice/
│
├── RecruitmentManagementServiceApplication.java   # Main entry point
│
├── config/
│   ├── DataInitializer.java              # Seeds default SUPER_ADMIN on startup
│   └── TwilioConfig.java                 # Twilio SMS client initialization
│
├── controller/
│   ├── AuthController.java               # /api/auth/** (public endpoints)
│   ├── SuperAdminController.java         # /api/super-admin/** 
│   ├── CooController.java                # /api/coo/**
│   ├── HrController.java                 # /api/hr/**
│   └── CandidateController.java          # /api/candidate/**
│
├── dto/
│   ├── request/
│   │   ├── LoginRequest.java
│   │   ├── OtpVerificationRequest.java
│   │   ├── RegisterUserRequest.java
│   │   ├── CreateCompanyRequest.java
│   │   ├── CreateHrRequest.java
│   │   ├── PostJobRequest.java
│   │   └── CandidateProfileRequest.java
│   └── response/
│       ├── AuthResponse.java
│       ├── OtpSentResponse.java
│       ├── UserResponse.java
│       ├── CompanyResponse.java
│       ├── JobResponse.java
│       ├── CandidateProfileResponse.java
│       ├── JobApplicationResponse.java
│       ├── ApiErrorResponse.java
│       └── MessageResponse.java
│
├── entity/
│   ├── User.java                         # Users table (all roles)
│   ├── Company.java                      # Companies table
│   ├── Job.java                          # Jobs with skillsets
│   ├── CandidateProfile.java             # Candidate profiles (tech/non-tech)
│   ├── JobApplication.java               # Job applications
│   └── OtpToken.java                     # OTP tokens for 2FA
│
├── enums/
│   ├── Role.java                         # SUPER_ADMIN, COO, HR, CANDIDATE
│   ├── JobCategory.java                  # TECHNICAL, NON_TECHNICAL
│   ├── CandidateCategory.java            # TECHNICAL, NON_TECHNICAL
│   └── ApplicationStatus.java            # APPLIED, SHORTLISTED, REJECTED
│
├── exception/
│   ├── ResourceNotFoundException.java    # 404 errors
│   ├── BadRequestException.java          # 400 errors
│   ├── OtpExpiredException.java          # OTP validation errors
│   └── GlobalExceptionHandler.java       # Centralized exception handling
│
├── repository/
│   ├── UserRepository.java
│   ├── CompanyRepository.java
│   ├── JobRepository.java
│   ├── CandidateProfileRepository.java
│   ├── JobApplicationRepository.java
│   └── OtpTokenRepository.java
│
├── security/
│   ├── JwtUtils.java                     # JWT token generation & validation
│   ├── JwtAuthenticationFilter.java      # Intercepts requests, validates JWT
│   ├── JwtAuthEntryPoint.java            # Handles 401 unauthorized responses
│   ├── CustomUserDetailsService.java     # Loads users from DB for Spring Security
│   └── SecurityConfig.java              # Security filter chain & role-based access
│
└── service/
    ├── AuthService.java
    ├── CompanyService.java
    ├── UserService.java
    ├── JobService.java
    ├── CandidateService.java
    ├── OtpService.java
    ├── EmailService.java
    ├── SmsService.java
    └── impl/
        ├── AuthServiceImpl.java
        ├── CompanyServiceImpl.java
        ├── UserServiceImpl.java
        ├── JobServiceImpl.java
        ├── CandidateServiceImpl.java
        ├── OtpServiceImpl.java
        ├── EmailServiceImpl.java
        └── SmsServiceImpl.java
```

---

## Prerequisites

Before running the application, ensure you have:

- **Java 21** (JDK) — [Download](https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html)
- **PostgreSQL 14+** — [Download](https://www.postgresql.org/download/)
- **Git** — [Download](https://git-scm.com/)
- **Gmail Account** with App Password (for email OTP) — [Guide](https://support.google.com/accounts/answer/185833)
- **Twilio Account** (optional, for SMS OTP) — [Sign Up Free](https://www.twilio.com/try-twilio)

---

## Setup & Installation

### 1. Clone the Repository

```bash
git clone https://github.com/sameerdighe28/recruitment-management-service.git
cd recruitment-management-service
```

### 2. Create PostgreSQL Database

```sql
-- Connect to PostgreSQL
psql -U postgres

-- Create the database
CREATE DATABASE cris;

-- Verify
\l
```

### 3. Configure Application Properties

Edit `src/main/resources/application.properties`:

```properties
# ============================
# PostgreSQL (REQUIRED)
# ============================
spring.datasource.url=jdbc:postgresql://localhost:5432/cris
spring.datasource.username=postgres
spring.datasource.password=your_postgres_password

# ============================
# Email OTP - Gmail SMTP (REQUIRED for email OTP)
# ============================
spring.mail.username=your-actual-email@gmail.com
spring.mail.password=your-16-char-app-password

# ============================
# SMS OTP - Twilio (OPTIONAL)
# ============================
twilio.account-sid=your-twilio-sid
twilio.auth-token=your-twilio-token
twilio.phone-number=+1234567890
twilio.enabled=false          # Set to true to enable SMS

# ============================
# JWT Secret (RECOMMENDED to change)
# ============================
app.jwt.secret=your-base64-encoded-secret-key
app.jwt.expiration-ms=86400000    # 24 hours

# ============================
# Default Super Admin
# ============================
app.admin.email=admin@recruitment.com
app.admin.password=Admin@123
app.admin.mobile=+1234567890
app.admin.name=Super Admin
```

#### 📧 How to Get Gmail App Password

1. Go to [Google Account Security](https://myaccount.google.com/security)
2. Enable **2-Step Verification**
3. Go to **App Passwords**
4. Select **Mail** → **Other (Custom name)** → Enter "Recruitment Service"
5. Copy the 16-character password into `spring.mail.password`

#### 📱 How to Get Twilio Credentials (Optional)

1. Sign up at [Twilio](https://www.twilio.com/try-twilio)
2. Get your **Account SID** and **Auth Token** from the dashboard
3. Get a phone number from **Phone Numbers** → **Manage** → **Buy a Number**
4. Set `twilio.enabled=true` in properties

> **Note:** With `twilio.enabled=false` (default), OTPs are logged to the console instead of being sent via SMS. Email OTP sending has a similar fallback if SMTP is not configured.

### 4. Build the Application

```bash
./gradlew clean build
```

### 5. Run the Application

```bash
./gradlew bootRun
```

The application will start on **http://localhost:8080**.

---

## Running the Application

### Using Gradle

```bash
# Build
./gradlew clean build

# Run
./gradlew bootRun

# Run tests
./gradlew test
```

### Using JAR

```bash
# Build JAR
./gradlew clean build

# Run JAR
java -jar build/libs/recruitment-management-service-0.0.1-SNAPSHOT.jar
```

### Verify Application is Running

```bash
curl -s http://localhost:8080/api/auth/login | jq .
# Should return 400 (expected — no body provided)
```

---

## Default Credentials

On first startup, a default **Super Admin** account is automatically created:

| Field | Value |
|---|---|
| Email | `admin@recruitment.com` |
| Password | `Admin@123` |
| Mobile | `+1234567890` |
| Role | `SUPER_ADMIN` |

> ⚠️ **Change these in production** by updating `app.admin.*` properties before first run.

---

## Sample API Requests

### 1. Login (Step 1 — Email + Password)

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@recruitment.com",
    "password": "Admin@123"
  }'
```

**Response:**
```json
{
  "message": "OTP has been sent to your email and mobile number",
  "email": "admin@recruitment.com",
  "maskedMobile": "****7890"
}
```

### 2. Verify OTP (Step 2 — Get JWT Token)

> Check your email or console logs for the 6-digit OTP

```bash
curl -X POST http://localhost:8080/api/auth/verify-otp \
  -H "Content-Type: application/json" \
  -d '{
    "email": "admin@recruitment.com",
    "otp": "123456"
  }'
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tokenType": "Bearer",
  "role": "SUPER_ADMIN",
  "email": "admin@recruitment.com"
}
```

### 3. Create a Company (Super Admin)

```bash
curl -X POST http://localhost:8080/api/super-admin/companies \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -d '{
    "name": "TechCorp Solutions",
    "address": "123 Tech Street, Bangalore",
    "website": "https://techcorp.com"
  }'
```

### 4. Create a COO (Super Admin)

```bash
curl -X POST http://localhost:8080/api/super-admin/coo \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <your-jwt-token>" \
  -d '{
    "email": "coo@techcorp.com",
    "password": "Coo@123",
    "fullName": "John COO",
    "mobileNumber": "+1234567891",
    "role": "COO"
  }'
```

### 5. Onboard an HR (COO)

```bash
curl -X POST http://localhost:8080/api/coo/hr \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <coo-jwt-token>" \
  -d '{
    "email": "hr@techcorp.com",
    "password": "Hr@123",
    "fullName": "Jane HR",
    "mobileNumber": "+1234567892",
    "companyId": "<company-uuid>"
  }'
```

### 6. Post a Job (HR)

```bash
curl -X POST http://localhost:8080/api/hr/jobs \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <hr-jwt-token>" \
  -d '{
    "title": "Senior Java Developer",
    "description": "Looking for experienced Java developer with Spring Boot expertise",
    "skillset": ["Java", "Spring Boot", "PostgreSQL", "Docker"],
    "category": "TECHNICAL",
    "location": "Bangalore"
  }'
```

### 7. Register as Candidate

```bash
curl -X POST http://localhost:8080/api/auth/register/candidate \
  -H "Content-Type: application/json" \
  -d '{
    "email": "candidate@example.com",
    "password": "Candidate@123",
    "fullName": "Alice Candidate",
    "mobileNumber": "+1234567893"
  }'
```

### 8. Create Candidate Profile (Choose Category)

```bash
curl -X POST http://localhost:8080/api/candidate/profile \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <candidate-jwt-token>" \
  -d '{
    "category": "TECHNICAL",
    "skills": ["Java", "Python", "AWS"],
    "resumeUrl": "https://example.com/resume.pdf",
    "experienceYears": 3
  }'
```

### 9. Apply to a Job (Candidate)

```bash
curl -X POST http://localhost:8080/api/candidate/jobs/<job-uuid>/apply \
  -H "Authorization: Bearer <candidate-jwt-token>"
```

---

## Database Schema

### Entity Relationship

```
┌──────────┐     ┌───────────┐     ┌──────────┐
│  Company  │◄────│   User    │────►│ OtpToken │
└──────────┘     └───────────┘     └──────────┘
     │                │
     │                │ (HR posts)
     ▼                ▼
┌──────────┐    ┌──────────────────┐
│   Job    │◄───│ CandidateProfile │
└──────────┘    └──────────────────┘
     │                │
     └────┐    ┌──────┘
          ▼    ▼
    ┌────────────────┐
    │ JobApplication  │
    └────────────────┘
```

### Tables

| Table | Description |
|---|---|
| `users` | All users (Super Admin, COO, HR, Candidate) |
| `companies` | Enlisted companies |
| `jobs` | Job postings with category |
| `job_skillsets` | Skills for each job (ElementCollection) |
| `candidate_profiles` | Candidate profiles with category |
| `candidate_skills` | Skills for each candidate (ElementCollection) |
| `job_applications` | Applications linking candidates to jobs |
| `otp_tokens` | OTP tokens for two-factor authentication |

---

## Troubleshooting

### ❌ Database Connection Error

```
Failed to configure a DataSource: 'url' attribute is not specified
```

**Fix:** Ensure PostgreSQL is running and the database `cris` exists:
```bash
psql -U postgres -c "CREATE DATABASE cris;"
```

### ❌ Email Sending Fails

```
Failed to send OTP email
```

**Fix:** The OTP will be logged to the console as a fallback. Check application logs:
```
FALLBACK - OTP for user@email.com: 123456
```

To fix email sending, configure Gmail App Password correctly in `application.properties`.

### ❌ JWT Token Expired

```json
{ "status": 401, "message": "Unauthorized: Full authentication is required" }
```

**Fix:** Login again to get a fresh JWT token (tokens expire after 24 hours).

### ❌ Access Denied (403)

```json
{ "status": 403, "message": "Access denied" }
```

**Fix:** Ensure your JWT token belongs to the correct role for the endpoint you're accessing.

### ❌ Port Already in Use

```bash
# Find and kill process on port 8080
lsof -ti:8080 | xargs kill -9

# Or run on a different port
./gradlew bootRun --args='--server.port=9090'
```

---

## License

This project is developed for internal recruitment management purposes.

---

## Author

**Sameer Dighe** — [GitHub](https://github.com/sameerdighe28)

