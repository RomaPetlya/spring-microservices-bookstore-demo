# Prompt 2: Generate E2E API Test Cases (REST + GraphQL)

You are an **Senior SDET**.  
Your goal is to create **comprehensive, detailed E2E API test cases** covering API endpoints.  
Focus **only on end-to-end scenarios** that validate the entire API workflow as experienced by a client or user.  

---

## Step 0 — Project Context Discovery (Auto-Scan)

Before analyzing documentation, **scan the project structure and source code** to collect all available technical details about APIs:

Typical directories to scan:

- /src
- /services
- /api
- /controllers
- /routes
- /models
- /graphql
- /schemas
- /resolvers

Extract and summarize:

- All **REST API endpoints** (paths, methods, parameters)
- All **GraphQL operations** (queries, mutations, subscriptions)
- Input/output data models or DTOs
- Error response structures
- Any **OpenAPI/Swagger** or **GraphQL schema (SDL)** definitions, if available

If some information is unclear or missing (e.g., undocumented endpoints, missing schemas, unclear field validations), **ask clarifying questions before proceeding**.

---

## Step 1 — Analyze Documentation Context

Use available project documentation (README or other context files) to understand:

- System architecture and service boundaries
- Core REST and GraphQL endpoints
- Data flow and dependencies
- Input/output formats, validation, and authentication flow
- Business rules and workflows
- Check documentation in `docs/ouputs/` folder for testing documentation.

Combine insights from both the **auto-scan** and **documentation** before defining the testing scope.

---

## Step 2 — Define Scope

Focus exclusively on **E2E API testing**, including:

1. REST API layer
2. GraphQL API layer
3. Use API Gateway service to send requests to the services.

Define:

- Which endpoints and operations will be covered in E2E scenarios
- Expected user workflows and data flows across multiple API calls
- Test levels: full E2E only (no isolated unit/integration API tests)
- Categories:
    - Positive & negative flows
    - Authentication & authorization (if applicable)
    - Data validation & schema conformance
    - Error handling & exceptions
    - Business rule validation
    - Boundary conditions (limits, pagination, query depth, rate limiting)

---

## Step 3 — Generate Test Cases

Create **detailed, atomic E2E test cases** in Markdown format.  

Each test case must include:

| ID | API Type (REST/GraphQL) | Endpoint / Operation | Description | Precondition | Request | Expected Response | Status Code | Type (E2E) | Category |

### Guidelines

- Cover **REST** endpoints with realistic E2E workflows.  
- Cover **GraphQL** queries and mutations, including:
    - Valid requests producing expected results
    - Invalid requests or missing fields
    - Type validation and query depth checks
    - Error response handling  
- Ensure **all test cases are automation-ready**:
    - Clear and reproducible steps
    - Explicit request/response data
    - Modularity and maintainability for future TAF implementation

---

## Step 4 — Output

Save the generated file in:  
`/docs/outputs/2_e2e_api_test_cases.md`

---

## Step 5 — Validation

Before finalizing:

- Verify endpoint and operation coverage (REST + GraphQL)
- Ensure logical consistency with workflows
- Confirm terminology matches documentation and code
- Remove duplicates or conflicting test cases
- Validate GraphQL operations against the schema (if available)
- Ensure all test cases are **ready for automation**:
    - Complete request/response details
    - Clear preconditions and expected results
    - Modular and maintainable for TAF

---

You can now start generating the **E2E API test cases** for REST and GraphQL endpoints, focusing only on full workflows and preparing them for future automation.
