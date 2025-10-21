package client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * REST client for E2E tests with structured logging
 */
public class RestClient {
    
    private static final Logger logger = LoggerFactory.getLogger(RestClient.class);
    private final ObjectMapper objectMapper;
    
    public RestClient() {
        this.objectMapper = new ObjectMapper();
        
        // Configure RestAssured defaults
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
    
    public <T> T get(String url, TypeReference<T> typeReference) {
        logger.debug("GET request to: {}", url);
        
        Response response = RestAssured
            .given()
            .contentType(ContentType.JSON)
            .when()
            .get(url);
        
        return handleResponse(response, typeReference, "GET", url);
    }
    
    public <T> T get(String url, Class<T> responseClass) {
        logger.debug("GET request to: {}", url);
        
        Response response = RestAssured
            .given()
            .contentType(ContentType.JSON)
            .when()
            .get(url);
        
        return handleResponse(response, responseClass, "GET", url);
    }
    
    public <T> T post(String url, Object requestBody, TypeReference<T> typeReference) {
        logger.debug("POST request to: {}", url);
        
        try {
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            logger.trace("Request body: {}", jsonBody);
            
            Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .when()
                .post(url);
            
            return handleResponse(response, typeReference, "POST", url);
        } catch (Exception e) {
            logger.error("Failed to serialize request body for POST {}: {}", url, e.getMessage());
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException("Failed to execute POST request: " + e.getMessage(), e);
        }
    }
    
    public <T> T post(String url, Object requestBody, Class<T> responseClass) {
        logger.debug("POST request to: {}", url);
        
        try {
            String jsonBody = requestBody instanceof String ? (String) requestBody : objectMapper.writeValueAsString(requestBody);
            logger.trace("Request body: {}", jsonBody);
            
            Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .when()
                .post(url);
            
            return handleResponse(response, responseClass, "POST", url);
        } catch (Exception e) {
            logger.error("Failed to execute POST request to {}: {}", url, e.getMessage());
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException("Failed to execute POST request: " + e.getMessage(), e);
        }
    }
    
    public <T> T delete(String url, Class<T> responseClass) {
        logger.debug("DELETE request to: {}", url);
        
        Response response = RestAssured
            .given()
            .contentType(ContentType.JSON)
            .when()
            .delete(url);
        
        // 404 is acceptable for DELETE operations
        if (response.getStatusCode() >= 400 && response.getStatusCode() != 404) {
            logger.warn("DELETE request to {} failed with status {}: {}", url, response.getStatusCode(), response.getBody().asString());
            throw new RuntimeException("HTTP " + response.getStatusCode() + ": " + response.getBody().asString());
        }
        
        if (response.getBody().asString().isEmpty()) {
            logger.debug("DELETE request to {} completed with empty response", url);
            return null;
        }
        
        return handleResponse(response, responseClass, "DELETE", url);
    }
    
    private <T> T handleResponse(Response response, TypeReference<T> typeReference, String method, String url) {
        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();
        
        if (statusCode >= 400) {
            logger.warn("{} request to {} failed - Status: {}, Body: {}", method, url, statusCode, responseBody);
            throw new RuntimeException("HTTP " + statusCode + ": " + responseBody);
        }
        
        logger.debug("{} request to {} successful - Status: {}", method, url, statusCode);
        logger.trace("Response body: {}", responseBody);
        
        try {
            return objectMapper.readValue(responseBody, typeReference);
        } catch (Exception e) {
            logger.error("Failed to parse {} response from {}: {}", method, url, e.getMessage());
            throw new RuntimeException("Failed to parse response: " + e.getMessage(), e);
        }
    }
    
    private <T> T handleResponse(Response response, Class<T> responseClass, String method, String url) {
        int statusCode = response.getStatusCode();
        String responseBody = response.getBody().asString();
        
        if (statusCode >= 400) {
            logger.warn("{} request to {} failed - Status: {}, Body: {}", method, url, statusCode, responseBody);
            throw new RuntimeException("HTTP " + statusCode + ": " + responseBody);
        }
        
        logger.debug("{} request to {} successful - Status: {}", method, url, statusCode);
        logger.trace("Response body: {}", responseBody);
        
        try {
            return objectMapper.readValue(responseBody, responseClass);
        } catch (Exception e) {
            logger.error("Failed to parse {} response from {}: {}", method, url, e.getMessage());
            throw new RuntimeException("Failed to parse response: " + e.getMessage(), e);
        }
    }
}