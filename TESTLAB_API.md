TestLab API Contract (Integration Reference)
1. System Overview

TestLab is a test management system that exposes a REST API used to manage:

Projects

Requirements (Epics → Features → User Stories)

Test Suites

Test Cases

Test Steps

Environments

Our DocGen system will consume this API to generate documentation.

2. Base API Information

Base URL

http://localhost:5000/api

Swagger Documentation

http://localhost:5000/api-docs

Authentication

NONE

All endpoints are currently public.

3. Data Hierarchy
Requirements Structure
Project
 └ Epic
     └ Feature
         └ UserStory
Testing Structure
Project
 └ TestSuite
     └ TestSuite (nested)
         └ TestCase
             ├ Preconditions
             └ TestSteps
Environment Structure
Project
 └ Environment
     └ EnvironmentVariables
4. Important IDs

All IDs use:

UUID

Example

"550e8400-e29b-41d4-a716-446655440000"

Your integration layer must treat external IDs as:

String

Never convert them to Long.

5. Key Endpoints Needed for Integration
5.1 Get Projects
GET /api/projects

Response

[
 {
   "id": "uuid",
   "name": "Project Name",
   "description": "Project description",
   "status": "ACTIVE"
 }
]

Used to list available projects.

5.2 Get Project Requirements
GET /api/specs/{projectId}

Returns full hierarchy

Epics
 └ Features
     └ UserStories

Example

[
 {
   "id": "epic-id",
   "name": "Authentication",
   "features": [
     {
       "id": "feature-id",
       "name": "Login",
       "userStories": [
         {
           "id": "story-id",
           "name": "User Login"
         }
       ]
     }
   ]
 }
]
5.3 Get Test Suites
GET /api/test-generation/test-suites?projectId={projectId}

Returns hierarchical test suites.

Example

[
 {
   "id": "suite-id",
   "name": "Authentication Tests",
   "children": [],
   "testCases": []
 }
]
5.4 Get Test Case Details
GET /api/test-generation/test-cases/{testCaseId}

Example

{
 "id": "uuid",
 "name": "Login Test",
 "summary": "Verify login works",
 "preconditions": [
   {
     "content": "User account exists"
   }
 ],
 "testSteps": [
   {
     "action": "Enter username",
     "expectedResult": "Username accepted"
   }
 ]
}
5.5 Get Environments
GET /api/environments/paginated?projectId={projectId}

Example

{
 "data": [
   {
     "id": "uuid",
     "name": "Development",
     "url": "https://dev.example.com"
   }
 ]
}
6. Core Objects
Project
{
 "id": "uuid",
 "name": "Project Name",
 "description": "Description",
 "status": "ACTIVE",
 "platforms": ["WEB"]
}
Epic
{
 "id": "uuid",
 "name": "Epic Name",
 "description": "Description",
 "status": "NEW",
 "priority": "LOW"
}
Feature
{
 "id": "uuid",
 "name": "Feature Name",
 "description": "Description"
}
User Story
{
 "id": "uuid",
 "name": "User Story",
 "description": "User story description",
 "status": "TO_DO"
}
Test Suite
{
 "id": "uuid",
 "name": "Suite Name",
 "parentId": "uuid",
 "children": []
}
Test Case
{
 "id": "uuid",
 "name": "Test Case",
 "summary": "Test case description"
}
Test Step
{
 "id": "uuid",
 "action": "Step action",
 "expectedResult": "Expected result"
}
7. Pagination Format

Paginated endpoints return:

{
 "data": [],
 "pagination": {
   "page": 1,
   "limit": 10,
   "total": 100,
   "totalPages": 10
 }
}
8. Important Integration Notes
IDs

All IDs are UUID strings.

Example

"550e8400-e29b-41d4-a716-446655440000"
Cascade Deletion

Deleting parent objects removes children.

Example

Delete Project
→ deletes Epics
→ deletes Features
→ deletes UserStories
Nested Structures

Some endpoints return nested objects.

Example

Epic
 └ Feature
     └ UserStory

Your integration layer should flatten or map them.

9. Integration Workflow

DocGen should follow this workflow:

Step 1

Fetch projects

GET /api/projects
Step 2

Fetch requirements

GET /api/specs/{projectId}
Step 3

Fetch test suites

GET /api/test-generation/test-suites?projectId={projectId}
Step 4

Fetch test case details if needed

GET /api/test-generation/test-cases/{testCaseId}
Step 5

Convert API data → DocGen internal models

Example

Epic → Requirement
Feature → Requirement
UserStory → Requirement
10. Data Format

All responses use

JSON

Dates format

ISO 8601

Example

2024-01-01T00:00:00.000Z
11. Integration Strategy

DocGen should not depend directly on TestLab structures.

Instead create:

TestLabAdapter

Responsible for converting:

TestLab API → DocGen Domain Models

Example

TestLab TestCase
→
DocumentTestCase
12. Minimal Endpoints Needed for DocGen

DocGen only requires:

GET /api/projects
GET /api/specs/{projectId}
GET /api/test-generation/test-suites?projectId={projectId}
GET /api/test-generation/test-cases/{testCaseId}

Everything else is optional.