# Test Automation Framework (TAF) Structure for Spring Microservices Bookstore

This document outlines the structure and design of the Test Automation Framework for testing the REST APIs, GraphQL endpoints, and inter-service communication in the Spring Microservices Bookstore project.

## 1. Project Folder Structure

```
api-test-automation/
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── bookstore/
│   │   │           ├── core/
│   │   │           │   ├── api/
│   │   │           │   │   ├── RestClient.java             # Base REST client
│   │   │           │   │   └── GraphQLClient.java          # Base GraphQL client
│   │   │           │   ├── config/
│   │   │           │   │   ├── Environment.java            # Environment configuration
│   │   │           │   │   ├── TestConfig.java             # Test configuration
│   │   │           │   │   └── TestProperties.java         # Properties loader
│   │   │           │   ├── kafka/
│   │   │           │   │   ├── KafkaConsumerClient.java    # Kafka consumer for event validation
│   │   │           │   │   └── KafkaProducerClient.java    # Kafka producer for event generation
│   │   │           │   ├── data/
│   │   │           │   │   ├── DataGenerator.java          # Dynamic test data generation
│   │   │           │   │   └── TestDataLoader.java         # JSON data loader
│   │   │           │   └── utils/
│   │   │           │       ├── DatabaseUtils.java          # DB interactions for setup/validation
│   │   │           │       ├── DockerUtils.java            # Docker control for service isolation
│   │   │           │       ├── AssertionUtils.java         # Custom assertions
│   │   │           │       └── RetryUtils.java             # Retry mechanisms
│   │   │           └── clients/                            # Service-specific clients
│   │   │               ├── BookServiceClient.java          # GraphQL client for book service
│   │   │               ├── AuthorServiceClient.java        # REST client for author service
│   │   │               ├── OrderServiceClient.java         # REST client for order service
│   │   │               ├── StockCheckServiceClient.java    # REST client for stock-check service
│   │   │               ├── MessageServiceClient.java       # Messaging client
│   │   │               └── ApiGatewayClient.java           # Client for API gateway testing
│   │   └── resources/
│   │       ├── config/
│   │       │   ├── application.yml                         # Default configuration
│   │       │   ├── application-local.yml                   # Local environment config
│   │       │   ├── application-ci.yml                      # CI environment config
│   │       │   ├── application-qa.yml                      # QA environment config
│   │       │   └── logback.xml                             # Logging configuration
│   │       ├── data/
│   │       │   ├── books/                                  # Book service test data
│   │       │   ├── authors/                                # Author service test data
│   │       │   ├── orders/                                 # Order service test data
│   │       │   └── stock/                                  # Stock check service test data
│   │       └── schemas/
│   │           └── graphql/                                # GraphQL schema for validation
│   │               └── book-schema.graphqls
│   │
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── bookstore/
│       │           ├── book/                               # Book service tests
│       │           │   ├── GetAllBooksTests.java
│       │           │   ├── CreateBookTests.java
│       │           │   └── DeleteBookTests.java
│       │           ├── author/                             # Author service tests
│       │           │   ├── GetAllAuthorsTests.java
│       │           │   ├── CreateAuthorTests.java
│       │           │   └── DeleteAuthorTests.java
│       │           ├── order/                              # Order service tests
│       │           │   └── PlaceOrderTests.java
│       │           ├── stock/                              # Stock check service tests
│       │           │   └── StockCheckTests.java
│       │           ├── gateway/                            # API gateway tests
│       │           │   └── GatewayRoutingTests.java
│       │           ├── integration/                        # Integration tests
│       │           │   ├── OrderStockIntegrationTests.java
│       │           │   ├── OrderMessageIntegrationTests.java
│       │           │   └── EndToEndOrderFlowTests.java
│       │           ├── resilience/                         # Resilience pattern tests
│       │           │   └── CircuitBreakerTests.java
│       │           └── testsuites/                         # Test suite definitions
│       │               ├── RegressionTestSuite.java
│       │               ├── SmokeTestSuite.java
│       │               └── ServiceTestSuites.java
│       └── resources/
│           ├── testng.xml                                  # TestNG configuration
│           ├── junit-platform.properties                   # JUnit configuration
│           └── allure.properties                           # Allure reporting config
│
├── docker/
│   ├── docker-compose.test.yml                             # Test environment docker-compose
│   └── test-data-init/                                     # Init scripts for test containers
│       ├── mongo-init/                                     # MongoDB init scripts
│       └── postgres-init/                                  # PostgreSQL init scripts
│
├── target/                                                 # Build output
│   └── allure-results/                                     # Allure report data
│
├── pom.xml                                                 # Maven configuration
├── README.md                                               # Project documentation
└── .github/
    └── workflows/
        └── test-automation.yml                             # GitHub Actions workflow
```

## 2. Logical Test Organization

### 2.1. Service-Based Organization

Tests are organized primarily by service, following the microservices architecture:

- **Book Service Tests** - GraphQL API tests for book management
- **Author Service Tests** - REST API tests for author management (reactive)
- **Order Service Tests** - REST API tests for order placement and management
- **Stock Check Service Tests** - REST API tests for inventory checking
- **API Gateway Tests** - Tests for gateway routing and resilience
- **Integration Tests** - Cross-service integration test scenarios

### 2.2. Test Categories

Within each service folder, tests are further organized by functional area or endpoint:

- **Positive Tests** - Tests for successful API operations
- **Negative Tests** - Tests for error conditions and validation
- **Edge Case Tests** - Tests for boundary conditions
- **Resilience Tests** - Tests for circuit breaker and retry mechanisms

### 2.3. Test Suites

Test suites are defined to group tests for specific execution contexts:

- **Smoke Test Suite** - Critical path tests for basic functionality verification
- **Regression Test Suite** - Comprehensive test suite for full regression testing
- **Service Test Suites** - Service-specific test suites for isolated testing

## 3. Test Data Management Strategy

### 3.1. JSON Test Data Files

- Test data is stored in JSON files under `src/main/resources/data/` organized by service
- Separate files exist for request payloads, expected responses, and test scenarios
- Data files use a naming convention that ties them to specific test cases

### 3.2. Dynamic Data Generation

- `DataGenerator` utility generates dynamic test data for unique values (UUIDs, timestamps)
- Builder patterns are used for creating complex test objects with flexible variations

### 3.3. Database Integration

- Testcontainers provides isolated database instances for testing
- Database initialization scripts in `docker/test-data-init` populate test data
- `DatabaseUtils` offers methods to set up and validate database state for tests

### 3.4. Kafka Test Events

- Test events for Kafka-related tests are defined as POJOs
- `KafkaProducerClient` and `KafkaConsumerClient` handle event publishing and validation

## 4. Environment Configuration

### 4.1. Configuration Properties

- Environment-specific properties are managed in YAML files (`application-{env}.yml`)
- Environment variables can override configuration for CI/CD integration
- `TestProperties` class provides typed access to configuration values

### 4.2. Service URLs and Endpoints

```yaml
services:
  book-service:
    url: http://localhost:8081
    graphql-endpoint: /graphql
  author-service:
    url: http://localhost:8085
  order-service:
    url: http://localhost:8082
  stock-check-service:
    url: http://localhost:8083
  message-service:
    url: http://localhost:8084
  api-gateway:
    url: http://localhost:8080
  kafka:
    bootstrap-servers: localhost:9092
    topics:
      order-placed: messageTopic
```

### 4.3. Test Execution Controls

```yaml
test:
  retry:
    max-attempts: 3
    backoff-ms: 1000
  timeout:
    default-seconds: 10
    integration-seconds: 30
  parallel:
    threads: 4
```

### 4.4. Environment Switching

- Command-line parameters allow selecting the environment profile
- CI/CD pipeline can set the appropriate environment via properties

## 5. API Client Layer

### 5.1. Base REST Client

- `RestClient` is a wrapper around RestAssured for standardized REST API interactions
- Provides methods for GET, POST, PUT, DELETE with standardized error handling
- Integrates with logging, assertions, and response validation

```java
public class RestClient {
    private RequestSpecification requestSpec;
    
    public Response get(String endpoint, Map<String, Object> queryParams);
    public Response post(String endpoint, Object requestBody);
    public Response delete(String endpoint, String id);
    // Other methods...
}
```

### 5.2. GraphQL Client

- `GraphQLClient` handles GraphQL-specific operations and validations
- Supports queries, mutations, and variables
- Handles GraphQL-specific error patterns

```java
public class GraphQLClient {
    private String graphqlEndpoint;
    
    public GraphQLResponse query(String queryName, String query, Map<String, Object> variables);
    public GraphQLResponse mutation(String mutationName, String mutation, Map<String, Object> variables);
    // Other methods...
}
```

### 5.3. Service-Specific Clients

- Service clients extend the base clients with service-specific operations
- Provide domain-specific methods that match the API's capabilities
- Handle service-specific validation and error patterns

```java
public class BookServiceClient extends GraphQLClient {
    public List<Book> getAllBooks();
    public Book createBook(BookRequest bookRequest);
    public boolean deleteBook(String id);
    // Other methods...
}
```

## 6. Logging and Reporting

### 6.1. Logging Strategy

- SLF4J with Logback for configurable logging
- HTTP request/response logging for API calls
- Structured logging format for machine parsing
- Different log levels for CI vs. local execution

### 6.2. Allure Reporting

- Allure for rich test reporting with attachments
- Custom annotations for categorizing and labeling tests
- HTTP request/response captured as attachments
- Test data linked to test executions
- Environment information included in reports

### 6.3. Test Execution Metrics

- Test execution time tracking
- Success/failure statistics
- Flaky test identification
- Test coverage mapping to requirements

## 7. CI/CD Integration

### 7.1. GitHub Actions Workflow

```yaml
name: API Test Automation

on:
  push:
    branches: [ main ]
  pull_request:
    branches: [ main ]
  schedule:
    - cron: '0 0 * * *'  # Daily run

jobs:
  test:
    runs-on: ubuntu-latest
    
    steps:
    - uses: actions/checkout@v2
    
    - name: Set up JDK 17
      uses: actions/setup-java@v2
      with:
        java-version: '17'
        distribution: 'adopt'
        
    - name: Start Test Environment
      run: docker-compose -f docker/docker-compose.test.yml up -d
        
    - name: Run Tests
      run: mvn clean test -Denv=ci
        
    - name: Generate Allure Report
      run: mvn allure:report
        
    - name: Publish Test Results
      uses: actions/upload-artifact@v2
      with:
        name: test-results
        path: target/allure-results
```

### 7.2. Test Environment Setup

- Docker Compose for isolated test environment
- Test-specific database instances with initialized data
- Kafka setup for messaging tests
- Service dependencies managed via Docker health checks

### 7.3. Test Selection for CI

- Tags for smoke vs. regression tests
- Automatic test selection based on code changes
- Parallelization to reduce execution time
- Failure retry logic for stability

## 8. Test Automation Best Practices

### 8.1. Code Structure

- Page Object Model adapted for API testing (Service Client pattern)
- Separation of test logic from test data
- Reusable components for common operations
- Builder pattern for complex request construction

### 8.2. Assertions and Validations

- Fluent assertions with custom matchers
- Schema validation for JSON/GraphQL responses
- Domain-specific validation helpers
- Detailed failure messages for debugging

### 8.3. Test Independence

- Each test is self-contained with its own setup/teardown
- No dependencies between test methods
- Parallel execution safety
- Test data isolation

### 8.4. Error Handling and Resilience

- Retry mechanisms for flaky external services
- Circuit breaker pattern for test stability
- Detailed error reporting with context
- Timeouts for long-running operations

## 9. Extensibility Design

### 9.1. Adding New Services

1. Create service-specific client extending appropriate base client
2. Add service configuration to environment properties
3. Create test data structures in resources
4. Implement test classes in appropriate package

### 9.2. Adding New Test Types

1. Extend base test class with new functionality
2. Create utility methods for the new test pattern
3. Add configuration properties if needed
4. Document the new test pattern

### 9.3. Integration with Other Tools

- TestRail integration for test case management
- Slack/Teams notifications for test failures
- Grafana dashboards for test metrics
- Containerization for portable test execution