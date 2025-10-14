# API Test Cases for Spring Microservices Bookstore Demo

This document contains detailed API test cases for the Spring Microservices Bookstore Demo project, covering REST APIs, GraphQL endpoints, and integration between services.

## Table of Contents

1. [Book Service GraphQL Tests](#book-service-graphql-tests)
2. [Author Service REST Tests](#author-service-rest-tests)
3. [Order Service REST Tests](#order-service-rest-tests)
4. [Stock Check Service REST Tests](#stock-check-service-rest-tests)
5. [Integration Tests](#integration-tests)
6. [API Gateway Tests](#api-gateway-tests)
7. [Error Handling & Resilience Tests](#error-handling--resilience-tests)

## Book Service GraphQL Tests

| ID | Service | API Type | Endpoint / Operation | Description | Precondition | Request | Expected Response | Status Code | Type | Category |
|----|---------|----------|---------------------|-------------|--------------|---------|-------------------|------------|------|----------|
| B-GQL-01 | Book Service | GraphQL | getAllBooks Query | Get all available books | Books exist in MongoDB | ```{ getAllBooks { id name description price } }``` | Array of book objects with all fields | 200 | Functional | Positive |
| B-GQL-02 | Book Service | GraphQL | getAllBooks Query - No Books | Get all books when none exist | No books in database | ```{ getAllBooks { id name description price } }``` | Empty array | 200 | Functional | Edge Case |
| B-GQL-03 | Book Service | GraphQL | createBook Mutation | Create a new book | - | ```mutation { createBook(bookRequest: {name: "Test Book", description: "Test description", price: 19.99}) { id name description price } }``` | Created book object with generated ID | 200 | Functional | Positive |
| B-GQL-04 | Book Service | GraphQL | createBook Mutation - Missing Required Fields | Attempt to create book with missing required field | - | ```mutation { createBook(bookRequest: {description: "Test description", price: 19.99}) { id name description price } }``` | GraphQL validation error | 400 | Functional | Negative |
| B-GQL-05 | Book Service | GraphQL | createBook Mutation - Invalid Price | Attempt to create book with invalid price format | - | ```mutation { createBook(bookRequest: {name: "Test Book", description: "Test description", price: "invalid"}) { id name description price } }``` | GraphQL validation error | 400 | Functional | Negative |
| B-GQL-06 | Book Service | GraphQL | deleteBook Mutation | Delete an existing book | Book exists in database | ```mutation { deleteBook(id: "existingId") }``` | true | 200 | Functional | Positive |
| B-GQL-07 | Book Service | GraphQL | deleteBook Mutation - Non-existing Book | Delete a non-existing book | Book with ID does not exist | ```mutation { deleteBook(id: "nonExistingId") }``` | false | 200 | Functional | Negative |
| B-GQL-08 | Book Service | GraphQL | getAllBooks Query - Partial Fields | Get only specific fields of books | Books exist in database | ```{ getAllBooks { id name } }``` | Array of book objects with only id and name fields | 200 | Functional | Positive |
| B-GQL-09 | Book Service | GraphQL | Invalid Field Selection | Request non-existent field | Books exist in database | ```{ getAllBooks { id name nonExistentField } }``` | GraphQL validation error | 400 | Functional | Negative |

## Author Service REST Tests

| ID | Service | API Type | Endpoint / Operation | Description | Precondition | Request | Expected Response | Status Code | Type | Category |
|----|---------|----------|---------------------|-------------|--------------|---------|-------------------|------------|------|----------|
| A-REST-01 | Author Service | REST | GET /api/authors | Get all authors | Authors exist in database | GET /api/authors | Array of author objects | 200 | Functional | Positive |
| A-REST-02 | Author Service | REST | GET /api/authors - Empty | Get all authors when none exist | No authors in database | GET /api/authors | Empty array | 200 | Functional | Edge Case |
| A-REST-03 | Author Service | REST | POST /api/authors | Create a new author | - | POST /api/authors with valid author JSON | Created author object with ID | 201 | Functional | Positive |
| A-REST-04 | Author Service | REST | POST /api/authors - Invalid | Create author with invalid data | - | POST /api/authors with invalid JSON | Validation error | 400 | Functional | Negative |
| A-REST-05 | Author Service | REST | DELETE /api/authors/{id} | Delete an existing author | Author exists in database | DELETE /api/authors/1 | Success response | 200 | Functional | Positive |
| A-REST-06 | Author Service | REST | DELETE /api/authors/{id} - Non-existent | Delete non-existent author | Author does not exist | DELETE /api/authors/999 | No content response | 204 | Functional | Negative |
| A-REST-07 | Author Service | REST | POST /api/authors - Missing Fields | Create author with missing required fields | - | POST /api/authors with missing required fields | Validation error | 400 | Functional | Negative |
| A-REST-08 | Author Service | REST | Reactive Response Handling | Verify reactive response handling | Authors exist in database | GET /api/authors | Non-blocking response with authors | 200 | Functional | Performance |

## Order Service REST Tests

| ID | Service | API Type | Endpoint / Operation | Description | Precondition | Request | Expected Response | Status Code | Type | Category |
|----|---------|----------|---------------------|-------------|--------------|---------|-------------------|------------|------|----------|
| O-REST-01 | Order Service | REST | POST /api/order | Create a valid order | Stock available for all items | POST /api/order with valid order JSON | Order creation success response | 201 | Functional | Positive |
| O-REST-02 | Order Service | REST | POST /api/order - Out of Stock | Create order for out-of-stock items | Items are out of stock | POST /api/order with valid order JSON | Error response about out of stock items | 500 | Functional | Negative |
| O-REST-03 | Order Service | REST | POST /api/order - Invalid Request | Create order with invalid request format | - | POST /api/order with invalid JSON | Validation error | 400 | Functional | Negative |
| O-REST-04 | Order Service | REST | POST /api/order - Empty Items | Create order with no items | - | POST /api/order with empty items array | Validation error | 400 | Functional | Negative |
| O-REST-05 | Order Service | REST | POST /api/order - Large Order | Create order with large number of items | Stock available for all items | POST /api/order with 100+ items | Order creation success response | 201 | Functional | Boundary |
| O-REST-06 | Order Service | REST | POST /api/order - Circuit Breaker | Create order when stock service is unavailable | Stock service is down | POST /api/order with valid order JSON | Circuit breaker fallback response | 503 | Functional | Resilience |
| O-REST-07 | Order Service | REST | POST /api/order - Retry Mechanism | Order service retries stock check | Intermittent stock service issues | POST /api/order with valid order JSON | Order creation success after retries | 201 | Functional | Resilience |

## Stock Check Service REST Tests

| ID | Service | API Type | Endpoint / Operation | Description | Precondition | Request | Expected Response | Status Code | Type | Category |
|----|---------|----------|---------------------|-------------|--------------|---------|-------------------|------------|------|----------|
| S-REST-01 | Stock Check Service | REST | GET /api/stockcheck | Check stock for existing items | Items exist in inventory | GET /api/stockcheck?skuCode=SKU1,SKU2 | Array of stock check responses | 200 | Functional | Positive |
| S-REST-02 | Stock Check Service | REST | GET /api/stockcheck - Non-existent | Check stock for non-existent items | Items do not exist in inventory | GET /api/stockcheck?skuCode=NONEXISTENT | Array with not-in-stock status | 200 | Functional | Negative |
| S-REST-03 | Stock Check Service | REST | GET /api/stockcheck - Empty | Check stock without providing SKU codes | - | GET /api/stockcheck | Validation error | 400 | Functional | Negative |
| S-REST-04 | Stock Check Service | REST | GET /api/stockcheck - Single Item | Check stock for a single item | Item exists in inventory | GET /api/stockcheck?skuCode=SKU1 | Single stock check response | 200 | Functional | Positive |
| S-REST-05 | Stock Check Service | REST | GET /api/stockcheck - Many Items | Check stock for a large number of items | Items exist in inventory | GET /api/stockcheck?skuCode=SKU1,SKU2,...SKU100 | Array of 100 stock check responses | 200 | Functional | Boundary |

## Integration Tests

| ID | Service | API Type | Endpoint / Operation | Description | Precondition | Request | Expected Response | Status Code | Type | Category |
|----|---------|----------|---------------------|-------------|--------------|---------|-------------------|------------|------|----------|
| INT-01 | Order + Stock Check | REST | POST /api/order → GET /api/stockcheck | Order service calls stock check service | Both services running, stock available | POST /api/order with valid order JSON | Order creation success, stock checked | 201 | Integration | Service-to-Service |
| INT-02 | Order + Stock Check | REST | POST /api/order → GET /api/stockcheck | Order service correctly handles out-of-stock | Stock not available | POST /api/order with items that are out of stock | Order failure with proper error | 500 | Integration | Service-to-Service |
| INT-03 | Order + Message | REST + Kafka | POST /api/order → Kafka | Order service publishes event to Kafka | Order service, Kafka running | POST /api/order with valid order JSON | Order created, event published to Kafka | 201 | Integration | Event-Driven |
| INT-04 | Message + Kafka | Kafka | Kafka → Message Service | Message service consumes order events | Message service, Kafka running | Event published to messageTopic | Message service processes event | N/A | Integration | Event-Driven |
| INT-05 | Book (GraphQL) + Order (REST) | GraphQL + REST | getAllBooks + POST /api/order | Order books retrieved from book service | All services running | 1. Get books via GraphQL 2. Order books via REST | Books retrieved and order placed | 200, 201 | Integration | Cross-Protocol |
| INT-06 | Order + Stock + Book | REST + GraphQL | End-to-end order flow | Complete order flow across multiple services | All services running | POST /api/order (after getting book info) | Order created, stock updated, event published | 201 | Integration | End-to-End |
| INT-07 | Order + Kafka + Message | REST + Kafka | Order notification flow | Test the complete order notification flow | All services running | POST /api/order with valid order JSON | Order created, notification event processed | 201 | Integration | Event-Driven |

## API Gateway Tests

| ID | Service | API Type | Endpoint / Operation | Description | Precondition | Request | Expected Response | Status Code | Type | Category |
|----|---------|----------|---------------------|-------------|--------------|---------|-------------------|------------|------|----------|
| GW-01 | API Gateway | REST | GET /api/authors (via Gateway) | Routing to Author Service | Gateway and Author Service running | GET /api/authors via gateway | Author service response | 200 | Functional | Routing |
| GW-02 | API Gateway | GraphQL | /graphql endpoint (via Gateway) | Routing to Book Service GraphQL | Gateway and Book Service running | GraphQL query via gateway | Book service GraphQL response | 200 | Functional | Routing |
| GW-03 | API Gateway | REST | GET /api/nonexistent | Route to non-existent service | Gateway running | GET /api/nonexistent | Not found error | 404 | Functional | Error Handling |
| GW-04 | API Gateway | REST | GET /api/authors (Service Down) | Handling service unavailability | Author service down | GET /api/authors via gateway | Service unavailable error | 503 | Functional | Resilience |
| GW-05 | API Gateway | REST | Cross-Origin Request | Test CORS configuration | Gateway running | Request with CORS headers | Proper CORS headers in response | 200 | Functional | Security |

## Error Handling & Resilience Tests

| ID | Service | API Type | Endpoint / Operation | Description | Precondition | Request | Expected Response | Status Code | Type | Category |
|----|---------|----------|---------------------|-------------|--------------|---------|-------------------|------------|------|----------|
| RES-01 | Order Service | REST | POST /api/order - Timeout | Circuit breaker timeout handling | Stock service slow to respond | POST /api/order with valid order JSON | Timeout error with fallback response | 503 | Functional | Resilience |
| RES-02 | Order Service | REST | POST /api/order - Retry Success | Retry mechanism eventual success | Stock service temporarily unavailable | POST /api/order with valid order JSON | Success after retries | 201 | Functional | Resilience |
| RES-03 | Order Service | REST | POST /api/order - Circuit Open | Behavior when circuit is open | Circuit breaker triggered by previous failures | POST /api/order with valid order JSON | Immediate fallback without calling stock service | 503 | Functional | Resilience |
| RES-04 | Order Service | REST | POST /api/order - Circuit Half-Open | Retry after circuit half-open | Circuit in half-open state | POST /api/order with valid order JSON | Test request passed through to check recovery | Varies | Functional | Resilience |
| RES-05 | API Gateway | REST | Retry Gateway Routing | Gateway retry on service failure | Service temporarily unavailable | Request through gateway | Success after gateway retry | 200 | Functional | Resilience |

---

## Validation Checklist

- ✅ Endpoint and operation coverage complete for all services
- ✅ Both REST and GraphQL APIs covered
- ✅ Positive and negative test cases included for each endpoint
- ✅ Boundary conditions tested
- ✅ Integration tests for service-to-service communication
- ✅ Event-driven communication via Kafka covered
- ✅ Resilience patterns (circuit breaker, retry) tested
- ✅ Error handling scenarios tested
- ✅ API Gateway routing functionality verified
- ✅ All test cases include clear request and expected response data