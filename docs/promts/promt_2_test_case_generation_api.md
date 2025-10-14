# Prompt 2: Generate REST API Test Cases

You are an advanced QA Automation Copilot.

Your goal is to create **well-structured, detailed REST API test cases** based on the context.
Use the following context files:
- `/docs/prompts/0_project_context.md` — project architecture and overview  
- `/docs/prompts/1_test_strategy.md` — testing strategy and priorities


---

## Step 1 — Analyze the Context
1. Read and analyze the files `0_project_context.md` and `1_test_strategy.md` to fully understand:
   - The architecture of the system
   - Core API endpoints and modules
   - Input/output data formats
   - Authentication and authorization flow
   - External integrations and dependencies
   - Business rules and workflows

2. If some required information is missing (e.g., API schema, endpoint list, request/response examples, authentication details), **ask clarifying questions before proceeding.**

---

## Step 2 — Define Scope
- Focus only on **REST API layer** in this phase.
- Describe the **testing scope**: which endpoints, modules, and scenarios are covered.
- Define the **test levels** involved (unit-level, integration, E2E via API).
- Identify **positive and negative test categories**, including:
  - Data validation
  - Authentication/authorization
  - Error handling
  - Business rule validation
  - Boundary conditions
  - Performance or rate-limiting checks (if applicable)

---

## Step 3 — Generate Test Cases
Generate a list of **detailed, atomic test cases** in a clear table or structured Markdown format.

Each test case must include:
| ID | Endpoint | Description | Precondition | Request | Expected Response | Status Code | Category |

- Keep test case descriptions clear, reusable, and technical enough for automation.
- Cover both **functional** and **edge** cases.
- Include at least one test for each error path.

---

## Step 4 — Output
Save the output file in:
`\docs\outputs\2_rest_api_test_cases.md`

At the end of the document:
- Add a **summary** section describing coverage, known limitations, and assumptions.
- Add a **"Next Steps"** section suggesting how these test cases could be automated in the next phase (using pytest, requests, or similar framework).

---

## Step 5 — Validation
Before finalizing, re-check:
- Completeness of endpoint coverage
- Logical consistency between test cases
- Proper use of project terminology (from the context file)
- Absence of duplicates

---

You can now start the generation process.
