package com.bookstore.core.api;

import com.bookstore.core.config.Environment;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * Base REST client for making HTTP requests.
 * Provides methods for common HTTP operations with standardized error handling.
 */
@Slf4j
public class RestClient {
    private final String baseUrl;
    private final Environment env = Environment.getInstance();
    
    /**
     * Creates a new RestClient with the specified base URL.
     *
     * @param baseUrl The base URL for all requests
     */
    public RestClient(String baseUrl) {
        this.baseUrl = baseUrl;
        log.info("Initialized REST client for base URL: {}", baseUrl);
    }
    
    /**
     * Creates a new RestClient for a specific service.
     *
     * @param serviceName The name of the service (book, author, order, stock, message, gateway)
     */
    public RestClient(String serviceName, boolean useGateway) {
        this.baseUrl = useGateway ? 
            env.getApiGatewayUrl() : 
            env.getServiceUrl(serviceName);
        log.info("Initialized REST client for service '{}' with base URL: {}", serviceName, baseUrl);
    }
    
    /**
     * Performs a GET request to the specified endpoint.
     *
     * @param endpoint The endpoint path (will be appended to the base URL)
     * @return The response
     */
    public Response get(String endpoint) {
        String url = ensureValidUrl(baseUrl);
        log.info("GET request to {}{}", url, endpoint);
        return createRequest().get(url + endpoint);
    }
    
    /**
     * Ensures that the URL is valid by checking if it starts with http:// or https://
     * 
     * @param url The URL to check and fix if necessary
     * @return A valid URL
     */
    private String ensureValidUrl(String url) {
        if (!url.startsWith("http://") && !url.startsWith("https://")) {
            return "http://" + url;
        }
        return url;
    }
    
    /**
     * Performs a GET request to the specified endpoint with query parameters.
     *
     * @param endpoint The endpoint path (will be appended to the base URL)
     * @param queryParams Map of query parameters
     * @return The response
     */
    public Response get(String endpoint, Map<String, Object> queryParams) {
        String url = ensureValidUrl(baseUrl);
        log.info("GET request to {}{} with query params", url, endpoint);
        return createRequest()
                .queryParams(queryParams)
                .get(url + endpoint);
    }
    
    /**
     * Performs a POST request to the specified endpoint.
     *
     * @param endpoint The endpoint path (will be appended to the base URL)
     * @param requestBody The request body object (will be serialized to JSON)
     * @return The response
     */
    public Response post(String endpoint, Object requestBody) {
        String url = ensureValidUrl(baseUrl);
        log.info("POST request to {}{}", url, endpoint);
        return createRequest()
                .body(requestBody)
                .post(url + endpoint);
    }
    
    /**
     * Performs a PUT request to the specified endpoint.
     *
     * @param endpoint The endpoint path (will be appended to the base URL)
     * @param requestBody The request body object (will be serialized to JSON)
     * @return The response
     */
    public Response put(String endpoint, Object requestBody) {
        String url = ensureValidUrl(baseUrl);
        log.info("PUT request to {}{}", url, endpoint);
        return createRequest()
                .body(requestBody)
                .put(url + endpoint);
    }
    
    /**
     * Performs a DELETE request to the specified endpoint.
     *
     * @param endpoint The endpoint path (will be appended to the base URL)
     * @return The response
     */
    public Response delete(String endpoint) {
        String url = ensureValidUrl(baseUrl);
        log.info("DELETE request to {}{}", url, endpoint);
        return createRequest().delete(url + endpoint);
    }
    
    /**
     * Performs a DELETE request to the specified endpoint with an ID parameter.
     *
     * @param endpoint The endpoint path (will be appended to the base URL)
     * @param id The ID to append to the URL
     * @return The response
     */
    public Response delete(String endpoint, String id) {
        String url = ensureValidUrl(baseUrl);
        String fullEndpoint = endpoint + "/" + id;
        log.info("DELETE request to {}{}", url, fullEndpoint);
        return createRequest().delete(url + fullEndpoint);
    }
    
    /**
     * Creates a basic request specification with common settings.
     *
     * @return The request specification
     */
    protected RequestSpecification createRequest() {
        return RestAssured.given()
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON);
    }
    
    /**
     * Gets the base URL for this client.
     *
     * @return The base URL
     */
    public String getBaseUrl() {
        return baseUrl;
    }
}