# Prompt 3: Design & Plan API Test Automation Framework (TAF)

You are an **Advanced QA Automation Copilot**.

Your goal is to design and plan a **Test Automation Framework (TAF)** for the project’s API. Automate E2E test cases located in - `/docs/outputs/2_e2e_api_Test_cases.md`

---

## Step 0 — Context and Input

Use the following context files:
- `/docs/prompts/0_project_context.md` — project architecture and overview  
- `/docs/prompts/1_test_strategy.md` — testing strategy, goals, and priorities  
- `/docs/outputs/2_e2e_api_Test_cases.md` — defined e2e api tests.

Analyze these files to fully understand:
- The architecture and technology stack of the project  
- The testing goals, priorities, and constraints  
- The nature and complexity of the defined test cases  

---

## Step 1 — Select Optimal Tech Stack

1. Review the test strategy and the system’s implementation language and stack.  
2. Choose the **most suitable automation stack** for API testing — for example:
   - **Python** → `pytest`, `requests`, `pytest-allure`, `pytest-asyncio`
   - **JavaScript/TypeScript** → `Playwright`, `Supertest`, `Allure`
   - **Java** → `RestAssured`, `JUnit5`, `Allure`
   - **C#** → `xUnit`, `RestSharp`, `Allure`
   - **Other** — if the strategy or codebase suggests a different stack

3. Justify the selected stack based on:
   - Project language compatibility
   - Framework maturity and ecosystem
   - Reporting and extensibility
   - Parallel execution and maintainability

---

## Step 2 — Framework Design and Structure

Analyze `/docs/outputs/2_e2e_api_Test_cases.md` and design a clean, modular framework architecture for these tests.

Produce a **structure plan** (save to `/docs/outputs/3_taf_structure.md`) that includes:
- Project folder structure (e.g. `/tests/api`, `/core`, `/data`, `/utils`, `/reports`)
- Logical grouping of tests by service/module
- Test data management strategy (fixtures, JSON files, DB mocks, etc.)
- Environment configuration (base URLs, tokens, secrets), for first attemt you can use only local environment
- Common API client layer or wrapper (e.g., RequestManager)
- Logging and reporting setup (e.g., Allure)

Follow principles like:
- Separation of test logic and data
- Reusable components and utility layers
- Config-driven test execution
- Scalability for additional services and API types

---

## Step 3 — Implementation Plan

Create a step-by-step **implementation roadmap** (save to `/docs/outputs/3_taf_plan.md`) describing how the TAF will be built.

Each step should include:
| Step | Description | Expected Output | Dependencies | Priority |
|------|--------------|-----------------|--------------|-----------|

Include key phases like:
1. Environment setup and dependency configuration (can be done only for local env) 
2. Framework scaffolding and folder structure creation  
3. Core API client and utilities implementation  
4. Authentication and environment handling  
5. Test case automation (based on `/2_rest_api_test_cases.md`)  
6. Allure or equivalent reporting integration  
8. Parallel and parameterized execution support  
9. Documentation and onboarding guide  

---

## Step 5 — Validation

Before finalizing:
- Verify that the proposed TAF structure fully supports E2E testing
- Ensure compatibility with CI/CD pipelines in the future and test data management  
- Confirm automation readiness of all generated test cases  
- Check that folder structure and naming conventions align with project standards  

---

You can now start designing and planning the API Test Automation Framework based on the provided documentation and test cases.