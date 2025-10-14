You are a **Senior SDET** responsible for designing a complete test strategy for this project.

Your goal is to create a detailed, professional **testing strategy document** based on the existing project overview.

---

### Input
Use the project context file located at:
`\docs\outputs\0_project_context.md`

Read and analyze it carefully — it contains all architecture, components, technologies, and goals of the system.

If something is unclear or missing, ask concise follow-up questions **only about the gaps** before generating the document.

---

### Objective
Produce a comprehensive **Test Strategy** describing *what, how, and why* the project will be tested.

This strategy must be realistic, technically detailed, and aligned with the system’s architecture, business goals, and constraints.

---

### Output format
Generate a **Markdown** document with clear structure and hierarchy.

Save it as:
`\docs\outputs\1_test_strategy.md`

---

### Required Structure

# 1. Introduction
Briefly restate what the project is and what testing will achieve.
Define the overall testing philosophy (risk-based, shift-left, automation-first, etc.).

# 2. Testing Scope
List what parts of the system are included and excluded from testing.
Break down the scope into API, UI, integrations, databases, infrastructure, and non-functional aspects.

# 3. Test Levels & Types
Describe which testing levels will be implemented (unit, integration, system, E2E, regression, UAT).
For each level, specify purpose, ownership, and automation feasibility.
Include special testing types: performance, security, accessibility, reliability.

# 4. Test Environment Strategy
Explain how environments are structured (dev, QA, staging, prod) and how data isolation and configuration are handled.
Describe test-data management, anonymization, and seeding strategies.

# 5. Test Data Management
Define how test data will be created, versioned, and reused.
Include sources (synthetic data, fixtures, DB snapshots, API mocks).

# 6. Tooling & Frameworks
List the tools, libraries, and frameworks that will be used for testing.
Provide reasoning for each choice (e.g., Pytest for API automation, Locust for performance, Postman for manual validation).

# 7. CI/CD Integration
Explain how testing fits into the CI/CD pipeline:
- When tests are executed (commit, PR, nightly, pre-release).
- Which stages block deployment.
- How reports and artifacts are stored.

# 8. Metrics & KPIs
Define how testing success will be measured:
- Test coverage (unit/API/E2E)
- Defect detection rate
- Pass/fail trend per build
- Mean time to detect/fix defects (MTTD/MTTR)
- Stability rate of automated tests

# 9. Risk Analysis & Mitigation
Identify potential risks (technical, process, data, integration, performance).
For each, suggest mitigation actions and ownership.

# 10. Deliverables
List all testing outputs:
- Test cases and suites
- Automation scripts
- Test reports and dashboards
- Defect logs and documentation updates

# 11. Continuous Improvement
Describe how the testing process will evolve (e.g., adding contract tests, CI enhancements, new automation layers).

---

### Final Instructions
- Keep the tone professional, concise, and technically grounded.
- Use bullet points and tables when possible.
- If the context file lacks certain details, infer them logically from the architecture.
- Do **not** describe implementation yet — this phase focuses only on *what* and *how* to test at a strategic level.

---

**Expected Output:**
A Markdown document `/docs/output/1_test_strategy.md` containing a full, actionable testing strategy ready for review.
