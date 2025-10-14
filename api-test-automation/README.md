# API Test Automation Framework

This is a comprehensive test automation framework for testing the Bookstore microservices application. It supports testing both REST and GraphQL APIs.

## Framework Structure

```
api-test-automation/
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── bookstore/
│   │               ├── core/
│   │               │   ├── api/
│   │               │   │   ├── GraphQLClient.java
│   │               │   │   └── RestClient.java
│   │               │   ├── config/
│   │               │   │   ├── Environment.java
│   │               │   │   ├── TestConfig.java
│   │               │   │   └── TestProperties.java
│   │               │   ├── data/
│   │               │   │   ├── DataGenerator.java
│   │               │   │   └── TestDataLoader.java
│   │               │   └── utils/
│   │               │       ├── AssertionUtils.java
│   │               │       └── RetryUtils.java
│   │               └── services/
│   │                   ├── AuthorServiceClient.java
│   │                   ├── BookServiceClient.java
│   │                   ├── OrderServiceClient.java
│   │                   └── StockCheckServiceClient.java
│   └── test/
│       ├── java/
│       │   └── com/
│       │       └── bookstore/
│       │           └── tests/
│       │               ├── author/
│       │               │   └── AuthorServiceTests.java
│       │               ├── book/
│       │               │   └── BookServiceTests.java
│       │               ├── integration/
│       │               │   └── EndToEndIntegrationTests.java
│       │               ├── order/
│       │               │   └── OrderServiceTests.java
│       │               └── stock/
│       │                   └── StockCheckServiceTests.java
│       └── resources/
│           ├── environments/
│           │   ├── docker.properties
│           │   └── local.properties
│           ├── testdata/
│           │   ├── authors.json
│           │   ├── books.json
│           │   ├── orders.json
│           │   └── stock.json
│           └── testng/
│               ├── all-tests.xml
│               ├── integration-tests.xml
│               └── service-tests.xml
└── pom.xml
```

## Features

- REST API testing with RestAssured
- GraphQL API testing with GraphQL Java Client
- Data-driven testing with JSON test data files
- Random test data generation using Java Faker
- Comprehensive assertion utilities
- Retry mechanism for flaky tests
- Allure reporting for beautiful test reports
- Parallel test execution with TestNG
- Environment-specific configuration

## Prerequisites

- Java 17 or later
- Maven 3.8.0 or later
- Running Bookstore microservices

## Running Tests

### Using Maven

Run all tests:
```bash
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/testng/all-tests.xml
```

Run only service tests:
```bash
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/testng/service-tests.xml
```

Run only integration tests:
```bash
mvn clean test -Dsurefire.suiteXmlFiles=src/test/resources/testng/integration-tests.xml
```

### Specifying Environment

To run tests against a specific environment, use the `-Denv` system property:

```bash
mvn clean test -Denv=local -Dsurefire.suiteXmlFiles=src/test/resources/testng/all-tests.xml
```

Supported environments:
- local (default)
- docker

## Generating Reports

Generate and open Allure reports:

```bash
mvn allure:report
mvn allure:serve
```

## Adding New Tests

1. Create a new test class in the appropriate package under `src/test/java/com/bookstore/tests/`
2. Add the test class to the TestNG XML files if needed
3. Run your tests using Maven

## Extending the Framework

### Adding a New Service Client

1. Create a new service client class in `src/main/java/com/bookstore/services/`
2. Implement the necessary API methods following the same pattern as the existing service clients

### Adding New Test Data

1. Create or update JSON files in `src/test/resources/testdata/`
2. Use the `TestDataLoader` utility to load the data in your tests

## Best Practices

- Use the provided utility classes for assertions and test data generation
- Add appropriate Allure annotations to your tests for better reporting
- Make tests independent of each other when possible
- Use the retry mechanism only for truly flaky tests
- Keep test methods focused on a single functionality