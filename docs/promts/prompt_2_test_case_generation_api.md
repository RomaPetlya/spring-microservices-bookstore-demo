# Prompt 2: Generate REST & GraphQL API + Inter-Service Integration Test Cases

You are an **Advanced QA Automation Copilot**.  
Your goal is to create **comprehensive, detailed API test cases**, covering both **REST** and **GraphQL** endpoints, as well as **integration flows between services**.

---

## Step 0 — Project Context Discovery (Auto-Scan)

Before analyzing documentation, **scan the project structure and source code** to collect all available technical details about APIs and service interactions.

Search in typical directories such as:

-   /src
-   /services
-   /api
-   /controllers
-   /routes
-   /models
-   /graphql
-   /schemas
-   /resolvers

Extract and summarize:

-   All **REST API endpoints** (paths, methods, parameters)
-   All **GraphQL operations** (queries, mutations, subscriptions)
-   Input/output data models or DTOs
-   Error response structures
-   Service-to-service REST or GraphQL calls
-   External API integrations
-   Any **OpenAPI/Swagger** or **GraphQL schema (SDL)** definitions, if available

If some information is unclear or missing (e.g., undocumented endpoints, missing schemas, unclear field validations), **ask clarifying questions before proceeding**.

---

## Step 1 — Analyze Documentation Context

Use the following context files:

-   `/docs/prompts/0_project_context.md` — project architecture and overview
-   `/docs/prompts/1_test_strategy.md` — testing strategy and priorities

Analyze both to understand:

-   System architecture and service boundaries
-   Core REST and GraphQL API endpoints
-   Inter-service communication patterns (sync via REST/GraphQL, async via events or queues)
-   Data flow and dependencies
-   Input/output formats, validation, and authentication flow
-   Business rules and workflows

Combine insights from both the **auto-scan** and the **documentation** before defining the testing scope.

---

## Step 2 — Define Scope

Focus exclusively on:

1. **REST API layer**
2. **GraphQL API layer**
3. **Integration between services** using these APIs.

Clearly define:

-   Which services, endpoints, and operations (queries/mutations) will be tested
-   Which inter-service interactions (e.g., Service A → Service B via REST or GraphQL) are included
-   What levels of testing are relevant:
    -   **Functional API tests**
    -   **Integration API tests**
    -   (Optional) **Contract verification**

Include categories such as:

-   Positive & negative test flows
-   Authentication & authorization if exist
-   Data validation & schema conformance
-   Error handling & exception propagation
-   Business rule validation
-   Boundary conditions (limits, pagination, query depth, rate limiting)
-   Multi-service scenarios (e.g., GraphQL mutation triggering REST update in another service)

---

## Step 3 — Generate Test Cases

Create **detailed, atomic test cases** in Markdown format.

Each test case must be in a table and include:

| ID | Service | API Type (REST/GraphQL) | Endpoint / Operation | Description | Precondition | Request | Expected Response | Status Code | Type (Functional / Integration) | Category |

### Guidelines

-   For **REST API** — cover all HTTP methods, endpoints, and standard response codes.
-   For **GraphQL API** — cover both **queries** and **mutations**, including:
    -   Valid requests with expected data
    -   Invalid field selections (non-existent, unauthorized fields)
    -   Type validation (wrong input types)
    -   Query depth and nested relationships
    -   Error response handling (e.g., partial success with `errors[]`)
-   Include **integration-level** tests that verify how one service’s API call affects another (REST ↔ REST, REST ↔ GraphQL, GraphQL ↔ GraphQL).
-   Use schemas and examples from scanned code whenever possible.
-   Ensure test descriptions are automation-friendly and modular.

---

## Step 4 — Output

Save the generated file in:
`/docs/outputs/2_api_test_cases.md`

---

## Step 5 — Validation

Before finalizing:

-   Check endpoint and operation coverage completeness (REST + GraphQL)
-   Ensure logical consistency between services and test cases
-   Verify terminology matches both documentation and code
-   Remove duplicates or conflicting tests
-   Confirm integration flows reflect the actual system behavior
-   Validate GraphQL operations against the schema (if available)
-   Ensure all test cases are **ready for automation**:
    -   Contain clear and reproducible steps
    -   Include all necessary request/response data
    -   Have explicit expected results and status codes
    -   Are modular and maintainable for future automation frameworks

---

You can now begin generating the **REST, GraphQL, and inter-service integration test cases**, using both the documentation and the live code analysis as your sources.
