# REST API Test Cases for Spring Microservices Bookstore

This document outlines the test cases for the REST API endpoints of the Spring Microservices Bookstore application. The test cases focus on the REST API layer, covering functional and edge cases for each endpoint.

## 1. Testing Scope

### Included Services

- **Author Service** (REST API with WebFlux)
- **Order Service** (REST API)
- **API Gateway** routing for both services

### Test Levels

1. **Component Testing**: Validating individual endpoints in isolation
2. **Integration Testing**: Testing endpoints with real database interactions
3. **E2E via API**: Testing complete user journeys through API calls

### Test Categories

1. **Functional Testing**: Validating expected behavior of endpoints
2. **Data Validation**: Testing input validation rules
3. **Error Handling**: Verifying proper error responses
4. **Business Rule Validation**: Ensuring business logic is correctly implemented
5. **Performance**: Basic response time validation
6. **Concurrency**: Handling multiple simultaneous requests
7. **Circuit Breaker**: Testing resilience patterns

## 2. Author Service Test Cases

### 2.1 Get All Authors

| ID | Endpoint | Description | Precondition | Request | Expected Response | Status Code | Category |
|----|----------|-------------|--------------|---------|-------------------|-------------|----------|
| AUTH-GET-001 | GET /api/authors | Retrieve all authors when authors exist | Database contains authors | GET request to /api/authors | Array of author objects with id, name, and birthDate | 200 OK | Functional |
| AUTH-GET-002 | GET /api/authors | Retrieve empty array when no authors exist | Database contains no authors | GET request to /api/authors | Empty array | 200 OK | Edge Case |
| AUTH-GET-003 | GET /api/authors | Verify pagination with limit parameter | Database contains >20 authors | GET request to /api/authors?limit=10 | 10 author objects returned with proper pagination info | 200 OK | Functional |
| AUTH-GET-004 | GET /api/authors | Verify response time is acceptable | Database contains >100 authors | GET request to /api/authors | Response within 300ms | 200 OK | Performance |
| AUTH-GET-005 | GET /api/authors | Simulate service unavailability | Database connection fails | GET request to /api/authors | Appropriate error message | 503 Service Unavailable | Error Handling |

### 2.2 Get Author by ID

| ID | Endpoint | Description | Precondition | Request | Expected Response | Status Code | Category |
|----|----------|-------------|--------------|---------|-------------------|-------------|----------|
| AUTH-GET-ID-001 | GET /api/authors/{id} | Retrieve specific author by ID | Author with ID exists | GET request to /api/authors/1 | Author object with matching ID | 200 OK | Functional |
| AUTH-GET-ID-002 | GET /api/authors/{id} | Attempt to retrieve non-existent author | Author with ID doesn't exist | GET request to /api/authors/999 | Error message indicating author not found | 404 Not Found | Error Handling |
| AUTH-GET-ID-003 | GET /api/authors/{id} | Attempt to retrieve with invalid ID format | N/A | GET request to /api/authors/abc | Error message indicating invalid ID format | 400 Bad Request | Data Validation |

### 2.3 Create Author

| ID | Endpoint | Description | Precondition | Request | Expected Response | Status Code | Category |
|----|----------|-------------|--------------|---------|-------------------|-------------|----------|
| AUTH-POST-001 | POST /api/authors | Create valid author | N/A | POST request with valid author JSON: `{"name": "Jane Doe", "birthDate": "1985-05-15"}` | Created author object with generated ID | 201 Created | Functional |
| AUTH-POST-002 | POST /api/authors | Attempt to create author with missing name | N/A | POST request with JSON: `{"birthDate": "1985-05-15"}` | Error message indicating name is required | 400 Bad Request | Data Validation |
| AUTH-POST-003 | POST /api/authors | Attempt to create author with invalid birth date | N/A | POST request with JSON: `{"name": "Jane Doe", "birthDate": "invalid-date"}` | Error message indicating invalid date format | 400 Bad Request | Data Validation |
| AUTH-POST-004 | POST /api/authors | Attempt to create author with empty name | N/A | POST request with JSON: `{"name": "", "birthDate": "1985-05-15"}` | Error message indicating name cannot be empty | 400 Bad Request | Data Validation |
| AUTH-POST-005 | POST /api/authors | Attempt to create author with future birth date | N/A | POST request with JSON: `{"name": "Jane Doe", "birthDate": "2100-01-01"}` | Error message indicating birth date cannot be in future | 400 Bad Request | Business Rule Validation |
| AUTH-POST-006 | POST /api/authors | Create author with max length name | N/A | POST request with JSON containing 255 character name | Created author object | 201 Created | Boundary Condition |
| AUTH-POST-007 | POST /api/authors | Attempt to create author with too long name | N/A | POST request with JSON containing 256+ character name | Error message indicating name too long | 400 Bad Request | Boundary Condition |

### 2.4 Delete Author

| ID | Endpoint | Description | Precondition | Request | Expected Response | Status Code | Category |
|----|----------|-------------|--------------|---------|-------------------|-------------|----------|
| AUTH-DEL-001 | DELETE /api/authors/{id} | Delete existing author | Author with ID exists | DELETE request to /api/authors/1 | Confirmation of deletion | 204 No Content | Functional |
| AUTH-DEL-002 | DELETE /api/authors/{id} | Attempt to delete non-existent author | Author with ID doesn't exist | DELETE request to /api/authors/999 | Error message indicating author not found | 404 Not Found | Error Handling |
| AUTH-DEL-003 | DELETE /api/authors/{id} | Attempt to delete with invalid ID format | N/A | DELETE request to /api/authors/abc | Error message indicating invalid ID format | 400 Bad Request | Data Validation |
| AUTH-DEL-004 | DELETE /api/authors/{id} | Verify deleted author cannot be retrieved | Author was just deleted | GET request to /api/authors/{deleted-id} | Author not found message | 404 Not Found | Functional |

## 3. Order Service Test Cases

### 3.1 Place Order

| ID | Endpoint | Description | Precondition | Request | Expected Response | Status Code | Category |
|----|----------|-------------|--------------|---------|-------------------|-------------|----------|
| ORDER-POST-001 | POST /api/order | Place valid order with in-stock items | Stock exists for items | POST request with JSON: `{"orderLineItemsDtoList": [{"skuCode": "design_patterns_gof", "price": 29, "quantity": 1}]}` | Order confirmation with order ID | 201 Created | Functional |
| ORDER-POST-002 | POST /api/order | Attempt to order out-of-stock item | Item is out of stock | POST request with JSON: `{"orderLineItemsDtoList": [{"skuCode": "mythical_man_month", "price": 39, "quantity": 1}]}` | Error message indicating item is out of stock | 400 Bad Request | Business Rule Validation |
| ORDER-POST-003 | POST /api/order | Place order with multiple items | Stock exists for all items | POST request with multiple line items | Order confirmation with order ID | 201 Created | Functional |
| ORDER-POST-004 | POST /api/order | Attempt to place order with empty line items | N/A | POST request with JSON: `{"orderLineItemsDtoList": []}` | Error message indicating line items required | 400 Bad Request | Data Validation |
| ORDER-POST-005 | POST /api/order | Attempt to place order with invalid SKU code | N/A | POST request with JSON: `{"orderLineItemsDtoList": [{"skuCode": "", "price": 29, "quantity": 1}]}` | Error message indicating invalid SKU | 400 Bad Request | Data Validation |
| ORDER-POST-006 | POST /api/order | Attempt to place order with negative price | N/A | POST request with JSON: `{"orderLineItemsDtoList": [{"skuCode": "design_patterns_gof", "price": -10, "quantity": 1}]}` | Error message indicating invalid price | 400 Bad Request | Data Validation |
| ORDER-POST-007 | POST /api/order | Attempt to place order with zero quantity | N/A | POST request with JSON: `{"orderLineItemsDtoList": [{"skuCode": "design_patterns_gof", "price": 29, "quantity": 0}]}` | Error message indicating invalid quantity | 400 Bad Request | Data Validation |
| ORDER-POST-008 | POST /api/order | Verify stock is decremented after successful order | Stock count is known | Place order and verify stock check | Stock count decreased by order quantity | N/A | Integration |

### 3.2 Order Service Circuit Breaker Tests

| ID | Endpoint | Description | Precondition | Request | Expected Response | Status Code | Category |
|----|----------|-------------|--------------|---------|-------------------|-------------|----------|
| ORDER-CB-001 | POST /api/order | Test circuit breaker when Stock Check Service is down | Stock Check Service unavailable | Place order for in-stock item | Fallback response or error with circuit breaker info | 503 Service Unavailable | Resilience |
| ORDER-CB-002 | POST /api/order | Verify circuit closes after Stock Check Service recovery | Stock Check Service recovers after failure | Place order for in-stock item | Normal successful response | 201 Created | Resilience |

## 4. API Gateway Routing Tests

| ID | Endpoint | Description | Precondition | Request | Expected Response | Status Code | Category |
|----|----------|-------------|--------------|---------|-------------------|-------------|----------|
| GATEWAY-001 | GET /api/authors | Verify API Gateway routes to Author Service | Gateway and Author Service running | GET request to gateway /api/authors | Same response as direct call to Author Service | 200 OK | Functional |
| GATEWAY-002 | POST /api/order | Verify API Gateway routes to Order Service | Gateway and Order Service running | POST request to gateway /api/order | Same response as direct call to Order Service | 201 Created | Functional |
| GATEWAY-003 | GET /api/unknown | Test handling of requests to unknown endpoints | Gateway running | GET request to gateway /api/unknown | Appropriate error message | 404 Not Found | Error Handling |

## 5. Performance Test Cases

| ID | Endpoint | Description | Precondition | Request | Expected Response | Status Code | Category |
|----|----------|-------------|--------------|---------|-------------------|-------------|----------|
| PERF-001 | GET /api/authors | Verify response time under normal load | System under normal load | GET request to /api/authors | Response time < 300ms | 200 OK | Performance |
| PERF-002 | POST /api/order | Verify order processing time | System under normal load | POST request to /api/order | Response time < 500ms | 201 Created | Performance |
| PERF-003 | GET /api/authors | Test concurrent requests | System under normal load | 10 concurrent requests to /api/authors | All requests successful with response time < 1s | 200 OK | Concurrency |

## 6. Summary

### Coverage Summary

- **Author Service**: 11 test cases covering CRUD operations, validations, and error scenarios
- **Order Service**: 10 test cases covering order creation, validations, and circuit breaker functionality
- **API Gateway**: 3 test cases for routing verification
- **Performance**: 3 test cases for response time and concurrency validation

### Assumptions

1. The Stock Check Service is integrated with the Order Service and verifies stock availability
2. The system does not currently implement authentication or authorization
3. All services implement proper input validation
4. Circuit breakers are configured to protect services from cascading failures

### Known Limitations

1. Limited testing of Stock Check Service as its specific endpoints are not fully documented
2. Error response formats may vary between services and need to be standardized
3. No specific rate limiting tests as the application doesn't specify limits

## 7. Next Steps

### Test Automation Strategy

1. **Framework Selection**:
   - Use RestAssured with JUnit 5 for Java services
   - Use RestAssured with kotlintest for Kotlin services
   - Consider Karate DSL for scenario-based testing

2. **Implementation Approach**:
   - Create base test classes for common functionality
   - Implement service-specific test classes extending the base
   - Use TestContainers for integration testing with real databases
   - Implement test data factories for consistent data generation

3. **CI/CD Integration**:
   - Run API tests as part of CI/CD pipeline
   - Use Newman for running Postman collections in CI
   - Generate HTML reports for test results

4. **Monitoring & Reporting**:
   - Integrate with test management system (TestRail)
   - Create custom dashboards for API test coverage
   - Track response time trends over time

### Additional Test Development

1. Contract testing between microservices using Spring Cloud Contract
2. Load testing using JMeter or k6
3. Automated security scanning for common API vulnerabilities
4. Integration with API documentation tools (Swagger/OpenAPI)