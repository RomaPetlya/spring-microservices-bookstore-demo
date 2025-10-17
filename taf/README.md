# TAF - Test Automation Framework

A streamlined E2E API Test Automation Framework for the Spring Microservices Bookstore application.

## Overview

This framework provides focused E2E testing capabilities for:
- **Book Service** (GraphQL API via API Gateway)
- **Author Service** (REST API via API Gateway) 
- **Order Service** (REST API via API Gateway)
- **Cross-service E2E workflows**
- **Stock validation** (tested via Order Service - Stock Check Service is internal)

**Architecture Note:** Stock Check Service is internal and communicates with Order Service via Feign Client. It is NOT exposed through API Gateway and should not be tested directly.

## Technology Stack

- **Java 17** - Core platform
- **Maven** - Build system with minimal dependencies
- **JUnit 5** - Testing framework
- **RestAssured 5.4.0** - HTTP client for API testing
- **Allure 2.25.0** - Test reporting with step annotations
- **Logback** - Comprehensive logging configuration

## Quick Start

### Prerequisites

- **Java 17** or higher
- **Maven 3.8** or higher  
- **All microservices running:**
  - API Gateway (port 8080)
  - Discovery Server (Eureka)
  - Config Server
  - Book Service, Author Service, Order Service
  - Stock Check Service (internal)

### Setup

1. Navigate to the TAF directory:
   ```bash
   cd taf
   ```

2. Compile the framework:
   ```bash
   mvn clean compile
   ```

## Quick Start Commands

## Complete TAF Command Reference

### Core Build and Test Commands

#### Full Clean Build and Test
```powershell
mvn clean compile test
```

#### Fast Test Run (skip rebuild)
```powershell
mvn test
```

#### Run Tests with Allure Report
```powershell
mvn clean test allure:serve
```

#### Compile Only (skip tests)
```powershell
mvn clean compile -DskipTests
```

### Running Tests

#### All E2E Tests
```bash
mvn test -Dtest='*E2ETest' -DfailIfNoTests=false
```

#### Specific Test Class
```bash
mvn test -Dtest=BookServiceE2ETest
mvn test -Dtest=AuthorServiceE2ETest  
mvn test -Dtest=OrderServiceE2ETest
mvn test -Dtest=CrossServiceWorkflowE2ETest
```

#### Run Single Test Method
```bash
mvn test -Dtest=BookServiceE2ETest#testCreateBook
mvn test -Dtest=OrderServiceE2ETest#testPlaceOrderSuccess
```

#### Run Tests with Specific Profiles
```bash
mvn test -Dspring.profiles.active=test
mvn test -Dtest.environment=local
```

### Debug and Troubleshooting

#### Verbose Output
```powershell
mvn test -X -Dtest=OrderServiceE2ETest
```

#### Skip Tests (build only)
```powershell
mvn clean compile -DskipTests
```

#### View Test Reports
```powershell
# Surefire reports (basic XML/HTML)
start target/surefire-reports

# Allure reports (detailed - if CLI installed)
mvn allure:serve
```

## Current Test Results

**Latest Execution Summary:**
- ✅ **21 tests total** 
- ✅ **19 tests passing** (90% success rate)
- ❌ **2 tests failing** (validation edge cases in Order Service)

**Test Coverage by Service:**
- ✅ **Author Service**: 6/6 tests passing 
- ✅ **Book Service**: 6/6 tests passing
- ✅ **Cross-Service Workflows**: 4/4 tests passing  
- ⚠️ **Order Service**: 3/5 tests passing

**Known Issues:**
- `O-E2E-004`: Order Service doesn't validate missing SKU
- `O-E2E-005`: Order Service allows orders with quantity=0

**Discovered Bugs:**
1. **O-E2E-004:** Order Service doesn't validate missing SKU
2. **O-E2E-005:** Order Service allows orders with quantity=0

## Allure Test Reports

### Prerequisites for Allure
1. **Install Allure Command Line Tool:**
   ```powershell
   # Via Scoop (recommended)
   scoop install allure
   
   # Or download from: https://github.com/allure-framework/allure2/releases
   ```

### Generate and View Reports

#### Option 1: Generate and Auto-Open Report
```bash
mvn clean test allure:serve
```
This will:
- Run all tests
- Generate Allure results in `target/allure-results/`
- Start local server and open report in browser automatically

#### Option 2: Generate Report Files Only
```bash
mvn clean test allure:report
```
Report location: `target/site/allure-maven-plugin/index.html`

#### Option 3: View Existing Results
```bash
allure serve target/allure-results
```
Use this to view reports from previous test runs without re-running tests

## Allure Test Reports ✅

### Quick Allure Commands

#### Super Fast (One Command)
```powershell
mvn test ; allure serve target/allure-results
```

#### Generate Static Report
```powershell
mvn test ; allure generate target/allure-results -o allure-report --clean
```

### Working Allure Commands

#### Option 1: Quick Test + Allure Report
```powershell
# Run tests and immediately open Allure report
mvn clean test ; allure serve target/allure-results
```

#### Option 2: Generate Static Allure Report
```powershell
# Generate static HTML report
mvn clean test
allure generate target/allure-results --clean -o target/allure-report
allure open target/allure-report
```

#### Option 3: Maven + Allure Integration
```powershell
# Use Maven Allure plugin (if working)
mvn clean test allure:serve
```

### Allure Features Available
- 📊 **Test execution statistics** and trends
- 🔍 **Detailed step-by-step test flows** with @Step annotations
- 📝 **Request/Response logging** for API calls  
- 🏷️ **Test categorization** by Epic, Feature, Story
- ⏱️ **Execution timing** and performance metrics
- 🐛 **Failure analysis** with detailed error messages and stack traces
- 📈 **History tracking** across test runs
- 🎯 **Flaky test detection** and retry information

### Prerequisites
✅ **Allure CLI**: Installed via `npm install -g allure-commandline`  
✅ **Node.js**: Version 22.20.0 detected

## Configuration

### Test Configuration

Key configuration file: `test/resources/application-test.properties`

```properties
# API Gateway endpoint
api.gateway.url=http://localhost:8080

# Service endpoints (via API Gateway)
api.books.endpoint=/api/graphql
api.authors.endpoint=/api/authors
api.orders.endpoint=/api/order
```
api.orders.endpoint=/api/order
```

## Framework Structure

```
taf/
├── .gitignore
├── pom.xml  
├── README.md
├── client/
│   └── RestClient.java                    # HTTP client wrapper
├── config/
│   └── TestConfig.java                   # Test configuration  
└── test/
    ├── AuthorServiceE2ETest.java         # 6 tests - Author CRUD
    ├── BookServiceE2ETest.java           # 6 tests - Book CRUD (GraphQL)
    ├── CrossServiceWorkflowE2ETest.java  # 4 tests - Cross-service workflows
    ├── OrderServiceE2ETest.java          # 5 tests - Order placement + stock
    └── resources/
        ├── allure.properties              # Allure configuration
        ├── application-test.properties    # Test environment config
        └── logback-test.xml              # Logging configuration
```

**Simple & Flat Structure:**
- ✅ No complex Maven src/main/java hierarchy
- ✅ No unnecessary package nesting  
- ✅ All files easily accessible
- ✅ Test resources contained within test folder

## Test Coverage

### ✅ Implemented Test Classes

1. **BookServiceE2ETest** - 6 tests covering:
   - B-E2E-001: List all books
   - B-E2E-002: Create book
   - B-E2E-003: Delete book  
   - B-E2E-004: Delete non-existent book
   - B-E2E-005: Create book with invalid data
   - B-E2E-006: Create book with negative price

2. **AuthorServiceE2ETest** - 6 tests covering:
   - A-E2E-001: List all authors
   - A-E2E-002: Create author
   - A-E2E-003: Delete author
   - A-E2E-004: Delete non-existent author
   - A-E2E-005: Create author with invalid date
   - A-E2E-006: Create author with future birth date

3. **OrderServiceE2ETest** - 5 tests covering:
   - O-E2E-001: Place order for in-stock item ✅
   - O-E2E-002: Order out-of-stock item ✅
   - O-E2E-003: Mixed stock order ✅
   - O-E2E-004: Missing SKU validation ❌ (bug found)
   - O-E2E-005: Zero quantity validation ❌ (bug found)

4. **CrossServiceWorkflowE2ETest** - 4 tests covering:
   - WF-E2E-001: Complete book lifecycle
   - WF-E2E-002: Author-Book relationship
   - WF-E2E-003: Stock validation via Order Service
   - WF-E2E-004: Order failure handling

## Framework Components

### Core Components

- **RestClient** - RestAssured wrapper with logging and error handling
- **TestConfig** - Centralized configuration management  
- **Allure Integration** - Step-by-step reporting with `@Step` annotations
- **Test Data Management** - Dynamic data creation and cleanup

## Architecture & Patterns

### Microservices Communication Flow
```
Client → API Gateway → [Book/Author/Order Service]
                    ↳ Order Service → (Feign Client) → Stock Check Service
```

**Key Principle:** TAF only tests externally exposed endpoints via API Gateway. Internal services (Stock Check) are tested indirectly through their public interfaces.

### Test Design Patterns

- **Simple Test Classes** - Direct test implementation without complex abstractions
- **Single RestClient** - One HTTP client for all API calls  
- **Step-by-Step Reporting** with Allure annotations
- **Independent Test Data** - each test creates and cleans up its own data
- **Flat Package Structure** - `client`, `config`, `test` packages without nesting

## Reporting & Logging

### Allure Reports
- Step-by-step test execution details
- Request/response logging
- Test execution history
- Failure analysis with screenshots

### Logging Configuration
- **Console output** for real-time monitoring
- **File logging** in `logs/` directory
- **API request/response** detailed logging
- **Debug level** logging for troubleshooting

## Troubleshooting

### Common Issues

1. **Connection refused**: Ensure API Gateway is running on `localhost:8080`
2. **Service discovery issues**: Check if all services are registered with Eureka
3. **Test failures**: Review logs in `logs/test-execution.log`
4. **Stock Check related failures**: Ensure Stock Check Service is running (tested via Order Service)

### Debugging Steps

1. Check service health endpoints
2. Review API Gateway routing configuration  
3. Verify all services are registered in Eureka
4. Check test logs for detailed error information
5. Use Allure reports for step-by-step analysis

## Contributing

### Adding New Tests

1. Follow existing naming convention: `{Service}E2ETest.java`
2. Use `@Step` annotations for Allure reporting
3. Implement proper test data cleanup
4. Test only via API Gateway endpoints
5. Add appropriate assertions and error handling

### Framework Guidelines

- **Clean Package Structure:** Simple `com.bookstore.taf` package hierarchy
- **Never test internal services directly** (e.g., Stock Check Service)
- **Always use API Gateway** as entry point  
- **Create unique test data** to avoid conflicts
- **Log requests/responses** for debugging
- **Follow existing code patterns** and structure

## Quality Assurance Results

The TAF successfully identified **2 genuine bugs** in the Order Service:
1. Missing SKU field validation
2. Zero quantity validation gap

This demonstrates the framework's effectiveness in finding real issues through comprehensive E2E testing.