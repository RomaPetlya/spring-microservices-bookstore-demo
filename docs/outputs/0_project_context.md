# Project Overview
Spring Microservices Bookstore is a demonstration application designed to illustrate patterns and practices used in building microservices with Spring Boot. It implements a simple bookstore system where users can browse books, manage authors, and place orders. The application showcases the integration of various technologies within a microservices architecture to build a scalable and maintainable system.

# Business Goals
1. Demonstrate best practices for building microservices using Spring technologies
2. Showcase service discovery, API gateway, and service communication patterns
3. Illustrate different approaches for service implementation (REST, GraphQL, reactive)
4. Demonstrate data management across different database types (SQL, NoSQL)
5. Provide a reference architecture for monitoring and tracing in microservices
6. Show containerization and orchestration of microservices with Docker and Kubernetes

# Technical Stack

## Programming Languages
- Java (Primary)
- Kotlin (Stock Check Service)
- TypeScript (Frontend)

## Frameworks & Libraries
- Spring Boot 3.2.1
- Spring Cloud (Netflix Eureka, Gateway, OpenFeign, Config)
- Spring WebFlux (Reactive) for Author Service
- Resilience4j (Circuit Breakers)
- Lombok (Boilerplate reduction)
- Next.js/React.js with Tailwind CSS (Frontend)
- GraphQL (Book Service)

## Databases
- MongoDB (Book Service)
- PostgreSQL (Author, Order, and Stock Check services)

## Messaging
- Apache Kafka with ZooKeeper

## Containerization & Orchestration
- Docker with Docker Compose
- Kubernetes

## Monitoring & Tracing
- Micrometer with Prometheus
- Grafana (Dashboards)
- Zipkin (Distributed Tracing)
- Spring Boot Actuator

## Build & Deployment
- Maven
- Google JIB (Docker image creation)

# System Components

## Infrastructure Services
1. **Discovery Server** (Port 8761): Netflix Eureka server for service registration and discovery
2. **Config Server** (Port 8888): Centralized configuration management for all microservices
3. **API Gateway** (Port 8080): Entry point for all client requests, handles routing to appropriate services

## Core Business Services
1. **Book Service** (Port 8081): Manages book data using MongoDB, exposes GraphQL API
2. **Author Service** (Port 8085): Manages author profiles using PostgreSQL, implements reactive programming with WebFlux
3. **Order Service** (Port 8082): Handles order processing using PostgreSQL
4. **Stock Check Service** (Port 8083): Manages inventory and stock validation using PostgreSQL, written in Kotlin
5. **Message Service** (Port 8084): Handles messaging through Kafka

## Supporting Services
1. **Zipkin** (Port 9411): Distributed tracing system
2. **Prometheus** (Port 9090): Metrics collection and alerting
3. **Grafana** (Port 3001): Visualization platform for metrics
4. **Frontend** (Port 3000): Next.js application providing user interface

## Databases
1. MongoDB for Book Service (Port 27017)
2. PostgreSQL instances:
   - Author Service (Port 5433)
   - Order Service (Port 5431)
   - Stock Check Service (Port 5432)

# User Roles
While the application doesn't implement full user authentication, the following roles can be identified:

1. **Customer**:
   - Browse books and authors
   - Place orders for books
   - View order status

2. **Administrator**:
   - Manage book inventory (add/delete books)
   - Manage author information (add/delete authors)
   - Monitor system health and metrics

3. **System Operator**:
   - Deploy and manage the application
   - Monitor service health
   - Analyze system metrics and traces

# Non-functional Requirements

## Performance
- Service response times should be within acceptable limits
- Resilience4j provides circuit breaking to handle failures gracefully
- Reactive programming (WebFlux) for non-blocking operations in Author Service

## Scalability
- Microservices can be independently scaled
- Service discovery allows dynamic scaling
- Containerization enables easy horizontal scaling

## Reliability
- Circuit breakers prevent cascade failures
- Health checks monitor service status

## Observability
- Distributed tracing with Zipkin
- Metrics collection with Prometheus
- Visualization with Grafana
- Spring Boot Actuator endpoints for health and metrics

## Security
- Note: Security is not fully implemented in this demo application
- Recommendation to implement Keycloak for identity and access management

## Compliance
- No specific compliance requirements mentioned (demo project)

# Environments

## Local Development
- Services run directly in IDE or via Maven commands
- Infrastructure components (databases, Kafka, etc.) run in Docker containers
- Supports debugging and rapid development

## Docker Containerized
- All services run in Docker containers
- Uses Docker Compose profiles to manage service startup
- Full integration with monitoring and tracing

## Kubernetes
- Complete deployment to Kubernetes
- Separate YAML files for infrastructure, discovery/config, and business services
- Supports scalability and container orchestration

# Testing Goals

## API Testing
- Verify GraphQL endpoints for Book Service
- Test REST endpoints for Author and Order services
- Validate circuit breaker behavior for fault tolerance

## Integration Testing
- Test interaction between services
- Verify event-driven messaging via Kafka
- Test service discovery and API gateway functionality

## Performance Testing
- Monitor service response times
- Test circuit breaker thresholds
- Evaluate reactive service performance

## End-to-End Testing
- Validate complete user flows
- Test frontend integration with backend services
- Verify monitoring and tracing capabilities

# Constraints
- The application is designed as a demonstration, not a production-ready system
- Security features are not fully implemented
- Limited business logic complexity (focuses on architectural patterns)

# CI/CD & Deployment

## Build Process
- Maven for Java/Kotlin services
- Google JIB for Docker image creation without Dockerfiles
- Docker build for frontend Next.js application

## Deployment Options
1. Docker Compose:
   - Phased deployment using profiles (infrastructure, discovery-config, services)
   - Simple docker-compose commands for startup

2. Kubernetes:
   - Deployment via kubectl commands
   - Infrastructure, discovery/config, and services can be deployed separately

# Known Risks or Issues
1. No implemented security layer (authentication, authorization)
2. Potential timing issues during service startup
3. Docker Hub rate limiting for unauthenticated users
4. Kafka connectivity issues between containers
5. Data persistence concerns when restarting containers
6. Limited error handling in demo flows

# Artifacts & References

## Documentation
- README.md: Main documentation with architecture overview and setup instructions
- docs/HOW_TO_RUN.md: Detailed instructions for running the application
- assets/spring-boot-microservices-diagram.jpg: System architecture diagram

## APIs
- Postman collection: assets/Spring Microservices Bookstore Demo.postman_collection.json
- GraphQL API for Book Service
- REST APIs for other services

## Configuration
- docker-compose.yml: Docker Compose configuration
- kubernetes/: Kubernetes deployment configurations
- prometheus/prometheus.yml: Monitoring configuration

## Dashboards
- assets/custom-dashboard-grafana.json: Grafana dashboard configuration

## Frontend
- frontend/: Next.js application source code