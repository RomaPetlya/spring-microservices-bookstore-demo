package client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.Response;

/**
 * Simple REST client for E2E tests
 */
public class RestClient {
    
    private final ObjectMapper objectMapper;
    
    public RestClient() {
        this.objectMapper = new ObjectMapper();
        
        // Configure RestAssured defaults
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }
    
    public <T> T get(String url, TypeReference<T> typeReference) {
        Response response = RestAssured
            .given()
            .contentType(ContentType.JSON)
            .when()
            .get(url);
        
        if (response.getStatusCode() >= 400) {
            throw new RuntimeException("HTTP " + response.getStatusCode() + ": " + response.getBody().asString());
        }
        
        try {
            return objectMapper.readValue(response.getBody().asString(), typeReference);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse response: " + e.getMessage(), e);
        }
    }
    
    public <T> T get(String url, Class<T> responseClass) {
        Response response = RestAssured
            .given()
            .contentType(ContentType.JSON)
            .when()
            .get(url);
        
        if (response.getStatusCode() >= 400) {
            throw new RuntimeException("HTTP " + response.getStatusCode() + ": " + response.getBody().asString());
        }
        
        try {
            return objectMapper.readValue(response.getBody().asString(), responseClass);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse response: " + e.getMessage(), e);
        }
    }
    
    public <T> T post(String url, Object requestBody, TypeReference<T> typeReference) {
        try {
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            
            Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .when()
                .post(url);
            
            if (response.getStatusCode() >= 400) {
                throw new RuntimeException("HTTP " + response.getStatusCode() + ": " + response.getBody().asString());
            }
            
            return objectMapper.readValue(response.getBody().asString(), typeReference);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException("Failed to execute POST request: " + e.getMessage(), e);
        }
    }
    
    public <T> T post(String url, Object requestBody, Class<T> responseClass) {
        try {
            String jsonBody = requestBody instanceof String ? (String) requestBody : objectMapper.writeValueAsString(requestBody);
            
            Response response = RestAssured
                .given()
                .contentType(ContentType.JSON)
                .body(jsonBody)
                .when()
                .post(url);
            
            if (response.getStatusCode() >= 400) {
                throw new RuntimeException("HTTP " + response.getStatusCode() + ": " + response.getBody().asString());
            }
            
            return objectMapper.readValue(response.getBody().asString(), responseClass);
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            throw new RuntimeException("Failed to execute POST request: " + e.getMessage(), e);
        }
    }
    
    public <T> T delete(String url, Class<T> responseClass) {
        Response response = RestAssured
            .given()
            .contentType(ContentType.JSON)
            .when()
            .delete(url);
        
        if (response.getStatusCode() >= 400 && response.getStatusCode() != 404) {
            throw new RuntimeException("HTTP " + response.getStatusCode() + ": " + response.getBody().asString());
        }
        
        if (response.getBody().asString().isEmpty()) {
            return null;
        }
        
        try {
            return objectMapper.readValue(response.getBody().asString(), responseClass);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse response: " + e.getMessage(), e);
        }
    }
}