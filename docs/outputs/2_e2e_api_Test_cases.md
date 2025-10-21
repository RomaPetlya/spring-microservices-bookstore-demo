# E2E API Test Cases for Spring Microservices Bookstore

This document contains detailed E2E API test cases for the Spring Microservices Bookstore application.

## Test Environment
- API Gateway endpoint: `http://localhost:8080`
- Required Services:
  - API Gateway Service
  - Discovery Server (Eureka)
  - Config Server
  - Book Service (GraphQL)
  - Author Service (REST)
  - Order Service (REST)
  - Stock Check Service (Internal - accessed via Order Service only)

**Architecture Note:** Stock Check Service is an internal microservice that communicates with Order Service via Feign Client. It is NOT exposed through the API Gateway and should not be tested directly via external endpoints.

## E2E API Test Cases

### Book Service (GraphQL API)

| ID | API Type | Endpoint / Operation | Description | Precondition | Request | Expected Response | Status Code | Type | Category |
|----|----------|---------------------|-------------|-------------|---------|------------------|------------|------|----------|
| B-E2E-001 | GraphQL | POST http://localhost:8080/api/graphql | List all available books | At least one book exists in the database | ```{ "query": "{ getAllBooks { id name description price } }" }``` | ```{ "data": { "getAllBooks": [ { "id": "book-id", "name": "Book Name", "description": "Book Description", "price": 29.99 } ] } }``` | 200 | E2E | Positive Flow |
| B-E2E-002 | GraphQL | POST http://localhost:8080/api/graphql | Add a new book to catalog | - | ```{ "query": "mutation($book: BookRequest!) { createBook(bookRequest: $book) { id name description price } }", "variables": { "book": { "name": "Test Book", "description": "Test Description", "price": 19.99 } } }``` | ```{ "data": { "createBook": { "id": "book-id", "name": "Test Book", "description": "Test Description", "price": 19.99 } } }``` | 200 | E2E | Positive Flow |
| B-E2E-003 | GraphQL | POST http://localhost:8080/api/graphql | Delete an existing book | Book with specific ID exists | ```{ "query": "mutation($id: ID!) { deleteBook(id: $id) }", "variables": { "id": "book-id-to-delete" } }``` | ```{ "data": { "deleteBook": true } }``` | 200 | E2E | Positive Flow |
| B-E2E-004 | GraphQL | POST http://localhost:8080/api/graphql | Try to delete non-existent book | No book with specified ID exists | ```{ "query": "mutation($id: ID!) { deleteBook(id: $id) }", "variables": { "id": "non-existent-id" } }``` | ```{ "data": { "deleteBook": false } }``` | 200 | E2E | Negative Flow |
| B-E2E-005 | GraphQL | POST http://localhost:8080/api/graphql | Create book with invalid data (missing required name) | - | ```{ "query": "mutation($book: BookRequest!) { createBook(bookRequest: $book) { id name description price } }", "variables": { "book": { "description": "Test Description", "price": 19.99 } } }``` | GraphQL validation error response | 400 | E2E | Data Validation |
| **B-E2E-006** | **GraphQL** | **POST http://localhost:8080/api/graphql** | **Create book with negative price - STRICT** | **-** | **```{ "query": "mutation($book: BookRequest!) { createBook(bookRequest: $book) { id name description price } }", "variables": { "book": { "name": "Negative Price Book", "description": "Test Description", "price": -19.99 } } }```** | **HTTP 400 Bad Request (MUST reject)** | **400** | **E2E** | **Critical Validation** |
| **B-E2E-007** | **GraphQL** | **POST http://localhost:8080/api/graphql** | **Create book with zero price - STRICT** | **-** | **```{ "query": "mutation($book: BookRequest!) { createBook(bookRequest: $book) { id name description price } }", "variables": { "book": { "name": "Zero Price Book", "description": "Test Description", "price": 0.0 } } }```** | **HTTP 400 Bad Request (MUST reject)** | **400** | **E2E** | **Critical Validation** |

### Author Service (REST API)

| ID | API Type | Endpoint / Operation | Description | Precondition | Request | Expected Response | Status Code | Type | Category |
|----|----------|---------------------|-------------|-------------|---------|------------------|------------|------|----------|
| A-E2E-001 | REST | GET http://localhost:8080/api/authors | Retrieve all authors | At least one author exists in the database | - | ```[{ "id": 1, "name": "Author Name", "birthDate": "1990-01-01" }]``` | 200 | E2E | Positive Flow |
| A-E2E-002 | REST | POST http://localhost:8080/api/authors | Create a new author | - | ```{ "name": "Test Author", "birthDate": "1985-05-15" }``` | ```{ "id": "author-id", "name": "Test Author", "birthDate": "1985-05-15" }``` | 201 | E2E | Positive Flow |
| A-E2E-003 | REST | DELETE http://localhost:8080/api/authors/{id} | Delete an existing author | Author with specific ID exists | - | - | 200 | E2E | Positive Flow |
| A-E2E-004 | REST | DELETE http://localhost:8080/api/authors/{id} | Delete non-existent author | No author with specified ID exists | - | - | 204 | E2E | Negative Flow |
| A-E2E-005 | REST | POST http://localhost:8080/api/authors | Create author with invalid date format | - | ```{ "name": "Invalid Date Author", "birthDate": "invalid-date" }``` | Error response with validation message | 400 | E2E | Data Validation |
| **A-E2E-006** | **REST** | **POST http://localhost:8080/api/authors** | **Create author with future birth date - STRICT** | **-** | **```{ "name": "Future Date Author", "birthDate": "2050-01-01" }```** | **HTTP 400 Bad Request (MUST reject)** | **400** | **E2E** | **Critical Validation** |
| **A-E2E-007** | **REST** | **POST http://localhost:8080/api/authors** | **Create author with null birth date - STRICT** | **-** | **```{ "name": "Author Without Birth Date", "birthDate": null }```** | **HTTP 400 Bad Request (MUST reject)** | **400** | **E2E** | **Critical Validation** |
| **A-E2E-008** | **REST** | **POST http://localhost:8080/api/authors** | **Create author with empty name - STRICT** | **-** | **```{ "name": "", "birthDate": "1980-01-01" }```** | **HTTP 400 Bad Request (MUST reject)** | **400** | **E2E** | **Critical Validation** |

### Order Service (REST API)

**Note:** Order Service internally calls Stock Check Service via Feign Client to verify inventory before placing orders. All stock-related functionality should be tested through Order Service endpoints.

| ID | API Type | Endpoint / Operation | Description | Precondition | Request | Expected Response | Status Code | Type | Category |
|----|----------|---------------------|-------------|-------------|---------|------------------|------------|------|----------|
| O-E2E-001 | REST | POST http://localhost:8080/api/order | Place an order for in-stock item | Item is in stock (design_patterns_gof) | ```{ "orderLineItemsDtoList": [{ "skuCode": "design_patterns_gof", "price": 29, "quantity": 1 }] }``` | ```{ "status": "success", "message": "Order placed successfully!" }``` | 201 | E2E | Positive Flow |
| O-E2E-002 | REST | POST http://localhost:8080/api/order | Attempt to order out-of-stock item | Item is out of stock (mythical_man_month) | ```{ "orderLineItemsDtoList": [{ "skuCode": "mythical_man_month", "price": 39, "quantity": 1 }] }``` | ```{ "status": "error", "message": "The service is busy or the book is not in stock. Please try again later." }``` | 500 | E2E | Negative Flow |
| O-E2E-003 | REST | POST http://localhost:8080/api/order | Place order with multiple items (mixed stock) | Mixed stock availability | ```{ "orderLineItemsDtoList": [{ "skuCode": "design_patterns_gof", "price": 29, "quantity": 1 }, { "skuCode": "mythical_man_month", "price": 39, "quantity": 1 }] }``` | ```{ "status": "error", "message": "The service is busy or the book is not in stock. Please try again later." }``` | 500 | E2E | Negative Flow |
| O-E2E-004 | REST | POST http://localhost:8080/api/order | Place order with invalid data (missing SKU) - STRICT | - | ```{ "orderLineItemsDtoList": [{ "price": 29, "quantity": 1 }] }``` | HTTP 400 Bad Request (MUST reject) | 400 | E2E | Critical Validation |
| O-E2E-005 | REST | POST http://localhost:8080/api/order | Place order with zero quantity - STRICT | - | ```{ "orderLineItemsDtoList": [{ "skuCode": "design_patterns_gof", "price": 29, "quantity": 0 }] }``` | HTTP 400 Bad Request (MUST reject) | 400 | E2E | Critical Validation |

## Cross-Service E2E Workflow Test Cases

| ID | API Type | Description | Test Steps | Expected Results | Type | Category |
|----|----------|-------------|------------|-----------------|------|----------|
| WF-E2E-001 | Mixed | Complete book lifecycle workflow | 1. Create a new book via GraphQL<br>2. Verify the book exists in the catalog<br>3. Place an order for the book<br>4. Delete the book | 1. Book is created successfully<br>2. Book appears in catalog<br>3. Order is placed successfully<br>4. Book is removed from catalog | E2E | Business Workflow |
| WF-E2E-002 | Mixed | Author-Book relationship workflow | 1. Create a new author<br>2. Create a new book<br>3. Query both to verify they exist<br>4. Place an order<br>5. Delete the author<br>6. Delete the book | All operations complete successfully | E2E | Business Workflow |
| WF-E2E-003 | Mixed | Order-based stock validation workflow | 1. Order in-stock item via Order Service<br>2. Verify order succeeds<br>3. Order out-of-stock item via Order Service<br>4. Verify order fails with appropriate error | 1. In-stock item order succeeds<br>2. Out-of-stock item order fails with proper error message | E2E | Business Workflow |
| WF-E2E-004 | Mixed | Order failure handling workflow | 1. Attempt order for out-of-stock item via Order Service<br>2. Verify order fails with appropriate error | 1. Order attempt for out-of-stock item<br>2. Order fails with detailed error message | E2E | Error Handling |
| WF-E2E-005 | Mixed | Invalid data cascade workflow - STRICT | 1. Try to create author with future birth date (MUST fail)<br>2. Try to create book with negative price (MUST fail)<br>3. Try to place order with missing SKU (MUST fail) | All validation failures properly cascaded across services with HTTP 400 errors | E2E | Critical Validation |
| WF-E2E-006 | Mixed | Cross-service data integrity validation - STRICT | 1. Create valid author with proper birth date<br>2. Create valid book with positive price<br>3. Place valid order with proper data<br>4. Verify all services accept valid data | All services accept valid data consistently, proper cleanup performed | E2E | Data Integrity |

## Implementation Notes

### Prerequisites
1. All services running (API Gateway, Discovery Server, Config Server, all microservices)
2. API Gateway accessible at `http://localhost:8080`
3. Test database in known state before test execution

### Architecture Guidelines
- Entry Point: All tests must use API Gateway (`http://localhost:8080`)
- Internal Services: Stock Check Service accessed only via Order Service Feign Client
- Test Data: Dynamic creation and cleanup for each test
- Test Independence: Each test should create its own data