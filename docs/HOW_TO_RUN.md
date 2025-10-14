# How to Run Spring Microservices Bookstore Demo

This guide provides step-by-step instructions for running the Spring Microservices Bookstore Demo application on your local machine.

## Prerequisites

Before starting, ensure you have:

- [Docker Desktop](https://www.docker.com/products/docker-desktop) installed and running
- [Java Development Kit (JDK) 17](https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html) or later
- [Maven](https://maven.apache.org/install.html)
- Docker Hub account (logged in via Docker Desktop)

### Important System Configuration

For proper Kafka operation, ensure your hosts file has the correct configuration:

1. If using Windows, verify that `host.docker.internal` resolves correctly. This should be automatic with Docker Desktop.
2. Docker networking must allow inter-container communication.
3. Ensure Docker is allocated sufficient memory (at least 4GB recommended) in Docker Desktop settings.

## Step 1: Build the Application

1. **Build the Java services:**

   ```powershell
   mvn clean package -DskipTests
   ```

   This command builds all the Spring Boot microservices using JIB and creates Docker images for them.

2. **Build the frontend application:**

   ```powershell
   docker build -t microservices-bookstore/nextjs-frontend:latest ./frontend
   ```

## Step 2: Launch the Application

You can launch the application in one of two ways:

### Option 1: Start All Services Together (Recommended)

To avoid potential timing and connection issues, it's recommended to start all services at once:

```powershell
docker compose --profile infrastructure --profile discovery-config --profile services up -d
```

This ensures that all services are deployed with proper network connections and proper discovery timing.

### Option 2: Launch in Phases

Alternatively, you can launch the application in phases:

#### 1. Start Infrastructure Services

```powershell
docker compose --profile infrastructure up -d
```

This starts:
- MongoDB (book-service database)
- PostgreSQL instances (for author-service, order-service, stock-check-service)
- Kafka and Zookeeper
- Zipkin (distributed tracing)

Wait for all infrastructure components to initialize (about 30-60 seconds).

#### 2. Start Service Discovery and Configuration

```powershell
docker compose --profile discovery-config up -d
```

This starts:
- Eureka Discovery Server
- Config Server

Wait for these services to start and register (about 15-30 seconds).

#### 3. Start Business Services and Frontend

```powershell
docker compose --profile services up -d
```

This starts:
- API Gateway
- Book Service
- Author Service
- Order Service
- Stock Check Service
- Message Service
- Frontend (Next.js)
- Prometheus and Grafana (monitoring)

## Step 3: Verify Services are Running

```powershell
docker compose ps
```

All containers should show status as "Up".

## Step 4: Access the Application

Once all services are up and running, access:

- **Frontend:** [http://localhost:3000](http://localhost:3000)
- **API Gateway:** [http://localhost:8080](http://localhost:8080)
- **Eureka:** [http://localhost:8761](http://localhost:8761)
- **Zipkin:** [http://localhost:9411](http://localhost:9411)
- **Prometheus:** [http://localhost:9090](http://localhost:9090)
- **Grafana:** [http://localhost:3001](http://localhost:3001)
  - Username: `admin`
  - Password: `password`

## Step 5: Testing the Application

### Using the Frontend

- Browse books and authors
- Add new books or authors
- Place orders
- Delete books or authors

### Using API Endpoints

You can test the API directly using Postman:

1. Import the collection from `assets/Spring Microservices Bookstore Demo.postman_collection.json`
2. Test the following operations:
   - View all books (GraphQL)
   - Save a book (GraphQL)
   - Delete a book (GraphQL)
   - View all authors (REST)
   - Save an author (REST)
   - Delete an author (REST)
   - Place an order (REST)

## Stopping the Application

To stop all services:

```powershell
docker compose down
```

To stop and remove all volumes (databases will be reset):

```powershell
docker compose down -v
```

## Troubleshooting

### Service not starting

Check the logs for the specific service:

```powershell
docker compose logs <service-name>
```

Example:
```powershell
docker compose logs api-gateway
```

### Kafka Broker and Message Service Issues

If you encounter issues with Kafka broker or message-service not starting properly:

1. Verify the Kafka configuration in docker-compose.yml has correct advertised listeners:
   ```yaml
   KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://host.docker.internal:9092,PLAINTEXT_INTERNAL://broker:29092
   KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT_INTERNAL
   ```

2. Check message-service connectivity to Kafka:
   ```powershell
   docker compose logs message-service
   ```

3. Ensure message-service can connect to Kafka with the proper broker address:
   ```powershell
   docker compose logs broker
   ```

4. If issues persist, try restarting all services together using the recommended approach in Option 1.

### Database connection issues

Ensure infrastructure services are fully initialized before starting other services.

### Rate limiting errors from Docker Hub

Log in to Docker Hub:

```powershell
docker login
```

### Service Communication Issues

If services cannot communicate with each other:

1. Verify that all services are registered with Eureka:
   ```powershell
   curl http://localhost:8761/eureka/apps
   ```

2. Check if the API Gateway is properly routing requests:
   ```powershell
   docker compose logs api-gateway
   ```

3. Verify Docker networking is working correctly:
   ```powershell
   docker network ls
   docker network inspect spring-microservices-bookstore-demo_default
   ```

## Additional Resources

- See the main [README.md](README.md) file for more details
- Check `assets` folder for Postman collections and diagrams