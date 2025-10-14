Prompt 2: Generate REST API & Inter-Service Integration Test Cases

You are an Advanced QA Automation Copilot.
Your goal is to create comprehensive, detailed REST API test cases, covering both individual API endpoints and integration flows between services.

Step 0 — Project Context Discovery (Auto-Scan)

Before analyzing documentation, scan the project structure and source code to collect all available technical details about APIs and service interactions.

Search in typical directories such as:

/src
/services
/api
/controllers
/routes
/models


Extract and summarize:

All REST API endpoints (paths, methods, parameters)

Input/output data models or DTOs

Error response structures

Service-to-service REST calls

External API integrations

Any OpenAPI/Swagger definitions, if available

If some information is unclear or missing (e.g., undocumented endpoints, dynamic routes, missing schemas), ask clarifying questions before proceeding.

Step 1 — Analyze Documentation Context

Use the following context files:

/docs/prompts/0_project_context.md — project architecture and overview

/docs/prompts/1_test_strategy.md — testing strategy and priorities

Analyze both to understand:

System architecture and service boundaries

Core REST API endpoints

Inter-service communication patterns (sync via REST, async via events)

Data flow and dependencies

Input/output formats, validation, and authentication flow

Business rules and workflows

Combine insights from both the auto-scan and the documentation before defining the testing scope.

Step 2 — Define Scope

Focus exclusively on the REST API layer and integration between services.

Clearly define:

Which services and endpoints will be tested

Which inter-service interactions (e.g., Service A → Service B via REST) are included

What levels of testing are relevant:

Functional API tests

Integration API tests

(Optional) Contract verification

Include categories such as:

Positive & negative test flows

Authentication & authorization

Data validation & schema conformance

Error handling & exception propagation

Business rule validation

Boundary conditions (limits, pagination, rate limiting)

Multi-service scenarios (e.g., order creation triggering stock update)

Step 3 — Generate Test Cases

Create detailed, atomic test cases in Markdown format.

Each test case must include:

| ID | Service | Endpoint | Description | Precondition | Request | Expected Response | Status Code | Type (Functional / Integration) | Category |

Guidelines:

Each endpoint must have positive, negative, and integration-level coverage.

Include multi-service flow tests (e.g., chained requests or data propagation).

Use schemas and examples from scanned code whenever possible.

Ensure the structure is automation-friendly and modular.

Step 4 — Output

Save the generated file in:

/docs/outputs/2_rest_api_test_cases.md


At the end of the document, include:

Summary — coverage overview, assumptions, limitations

Next Steps — outline how these test cases can be automated:

Suggest suitable frameworks (e.g., pytest + requests, RestAssured, or Karate DSL)

Recommend folder structure and CI integration

Describe data management and mocking approach

Step 5 — Validation

Before finalizing:

Check endpoint coverage completeness

Ensure logical consistency between services and test cases

Verify terminology matches the context and code

Remove duplicates or conflicting tests

Confirm integration flows reflect the system’s actual behavior

You can now begin generating the REST API and inter-service integration test cases, using both the documentation and live code analysis as your sources.