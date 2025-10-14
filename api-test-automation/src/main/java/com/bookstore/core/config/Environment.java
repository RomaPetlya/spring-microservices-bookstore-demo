package com.bookstore.core.config;

import lombok.Getter;

/**
 * Environment configuration for the test framework.
 * Provides access to environment-specific settings like service URLs.
 */
@Getter
public class Environment {
    private static Environment instance;
    private final TestProperties properties;
    
    // Service URLs
    private final String bookServiceUrl;
    private final String bookServiceGraphqlEndpoint;
    private final String authorServiceUrl;
    private final String orderServiceUrl;
    private final String stockCheckServiceUrl;
    private final String messageServiceUrl;
    private final String apiGatewayUrl;
    
    // Kafka config
    private final String kafkaBootstrapServers;
    private final String orderPlacedTopic;
    
    // Test execution settings
    private final int retryMaxAttempts;
    private final int retryBackoffMs;
    private final int defaultTimeoutSeconds;
    private final int integrationTimeoutSeconds;
    private final int parallelThreads;

    private Environment() {
        properties = TestProperties.getInstance();
        
        // Load service URLs
        bookServiceUrl = properties.getProperty("services.book-service.url", "http://localhost:8081");
        bookServiceGraphqlEndpoint = properties.getProperty("services.book-service.graphql-endpoint", "/graphql");
        authorServiceUrl = properties.getProperty("services.author-service.url", "http://localhost:8085");
        orderServiceUrl = properties.getProperty("services.order-service.url", "http://localhost:8082");
        stockCheckServiceUrl = properties.getProperty("services.stock-check-service.url", "http://localhost:8083");
        messageServiceUrl = properties.getProperty("services.message-service.url", "http://localhost:8084");
        apiGatewayUrl = properties.getProperty("services.api-gateway.url", "http://localhost:8080");
        
        // Load Kafka config
        kafkaBootstrapServers = properties.getProperty("services.kafka.bootstrap-servers", "localhost:9092");
        orderPlacedTopic = properties.getProperty("services.kafka.topics.order-placed", "messageTopic");
        
        // Load test execution settings
        retryMaxAttempts = properties.getIntProperty("test.retry.max-attempts", 3);
        retryBackoffMs = properties.getIntProperty("test.retry.backoff-ms", 1000);
        defaultTimeoutSeconds = properties.getIntProperty("test.timeout.default-seconds", 10);
        integrationTimeoutSeconds = properties.getIntProperty("test.timeout.integration-seconds", 30);
        parallelThreads = properties.getIntProperty("test.parallel.threads", 4);
    }
    
    /**
     * Gets the singleton instance of the Environment.
     *
     * @return The Environment instance
     */
    public static synchronized Environment getInstance() {
        if (instance == null) {
            instance = new Environment();
        }
        return instance;
    }
    
    /**
     * Gets the base URL for a specific service.
     *
     * @param serviceName The name of the service (book, author, order, stock, message, gateway)
     * @return The base URL for the specified service
     */
    public String getServiceUrl(String serviceName) {
        switch (serviceName.toLowerCase()) {
            case "book":
                return bookServiceUrl;
            case "author":
                return authorServiceUrl;
            case "order":
                return orderServiceUrl;
            case "stock":
                return stockCheckServiceUrl;
            case "message":
                return messageServiceUrl;
            case "gateway":
                return apiGatewayUrl;
            default:
                throw new IllegalArgumentException("Unknown service name: " + serviceName);
        }
    }
    
    /**
     * Gets the GraphQL endpoint URL for the Book service.
     *
     * @return The complete GraphQL endpoint URL
     */
    public String getBookServiceGraphqlUrl() {
        return bookServiceUrl + bookServiceGraphqlEndpoint;
    }
}