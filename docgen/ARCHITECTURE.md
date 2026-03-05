1️⃣ Project Overview

This project is a Document Generation System that:

Loads Test Plans from database

Applies filters

Computes statistics

Generates documents in:

HTML

PDF

Word

Excel

The system follows a clean layered architecture using:

Controller layer

Service layer

DAO layer

Repository layer

Mapper layer (The Mapper converts database entities into document-specific models (DTOs).)

Filter layer (Applies filtering logic on the document data.)

Export layer




2️⃣ High-Level Architecture

User → Controller → Service → DAO → Repository → Database

Then:

Service → Mapper → Filter → Statistics (Computes metrics to display in the document.) → Template Engine → Export Service



3️⃣ Example Request Flow
User calls:
GET /generate/html/{id}
Step 1 — Controller

TestPlanController

Receives request

Calls DocumentGenerationService


Step 2 — Service

DocumentGenerationServiceImpl

Loads TestPlan via DAO

Converts entity to Document DTO

Applies filters

Computes statistics

Builds Thymeleaf context

Generates HTML

If needed → calls Export services


Step 3 — DAO Layer

TestPlanDaoImpl

Wraps repository access

Calls:

TestPlanRepository

TestCaseRepository

TestExecutionRepository


Step 4 — Repository Layer

Spring Data JPA interfaces:

TestPlanRepository

TestCaseRepository

TestExecutionRepository

TemplateConfigRepository

These communicate directly with the database.



Step 5 — Mapper Layer

DocumentMapper

Converts Entities → DTOs

Separates persistence model from document model

Example:

TestPlan → DocumentTestPlan



Step 6 — Filter Layer

DocumentFilterServiceImpl

Applies:

Status filtering

Priority filtering (future)

Execution filtering



Step 7 — Statistics Layer

StatisticsServiceImpl

Computes:

Total test cases

Passed

Failed

Execution rate

Coverage %



Step 8 — Template Layer

Uses:

resources/templates/document-template.html

Thymeleaf injects data into template.

Produces final HTML.

Step 9 — Export Layer

HTML can be transformed into:

PdfExportServiceImpl

WordExportServiceImpl

ExcelExportServiceImpl


4️⃣ Package Responsibilities
controller

Handles HTTP requests.

service

Contains business logic.

DAO

Wraps repository access.
Prepares fully loaded domain objects.

repository

Database access layer (Spring Data JPA).

document.entity

Database entities.

document.dto

Document-specific models.

mapper

Converts Entity → DTO.

filter

Applies document-level filtering logic.

statistics

Calculates document metrics.

export

Generates final document formats.

template

Template configuration and loader.



5️⃣ Why This Architecture Is Good

✔ Clear separation of concerns
✔ Easy to maintain
✔ Easy to extend
✔ Testable layers
✔ Follows clean architecture principles
✔ Avoids tight coupling



6️⃣ Current Data Flow Summary
Controller
↓
DocumentGenerationService
↓
DAO
↓
Repository
↓
Database
↑
Mapper
↑
Filter
↑
Statistics
↑
Template Engine
↑
Export Services