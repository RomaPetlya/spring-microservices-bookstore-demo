package com.bookstore.services;

import com.bookstore.core.api.RestClient;
import com.bookstore.core.config.Environment;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.util.Map;

/**
 * Client for interacting with the Author Service API.
 */
@Slf4j
public class AuthorServiceClient {
    private final RestClient restClient;
    private final String baseUrl;
    
    public AuthorServiceClient() {
        Environment env = Environment.getInstance();
        this.baseUrl = env.getAuthorServiceUrl();
        this.restClient = new RestClient(this.baseUrl);
        log.info("Initialized AuthorServiceClient with base URL: {}", baseUrl);
    }
    
    /**
     * Creates a new author.
     *
     * @param name The author's name
     * @param bio The author's biography
     * @param email The author's email
     * @return The response containing the created author
     */
    @Step("Create author with name: {0}, email: {2}")
    public Response createAuthor(String name, String bio, String email) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("name", name);
        requestBody.put("bio", bio);
        requestBody.put("email", email);
        
        log.info("Creating author with name: {}", name);
        return restClient.post("/api/v1/authors", requestBody.toString());
    }
    
    /**
     * Gets an author by ID.
     *
     * @param authorId The author ID
     * @return The response containing the author
     */
    @Step("Get author by ID: {0}")
    public Response getAuthorById(String authorId) {
        log.info("Getting author with ID: {}", authorId);
        return restClient.get("/api/v1/authors/" + authorId);
    }
    
    /**
     * Gets all authors.
     *
     * @return The response containing all authors
     */
    @Step("Get all authors")
    public Response getAllAuthors() {
        log.info("Getting all authors");
        return restClient.get("/api/v1/authors");
    }
    
    /**
     * Updates an author.
     *
     * @param authorId The author ID
     * @param updates Map of fields to update and their new values
     * @return The response containing the updated author
     */
    @Step("Update author with ID: {0}")
    public Response updateAuthor(String authorId, Map<String, Object> updates) {
        JSONObject requestBody = new JSONObject();
        updates.forEach(requestBody::put);
        
        log.info("Updating author with ID: {}", authorId);
        return restClient.put("/api/v1/authors/" + authorId, requestBody.toString());
    }
    
    /**
     * Deletes an author.
     *
     * @param authorId The author ID
     * @return The response from the delete operation
     */
    @Step("Delete author with ID: {0}")
    public Response deleteAuthor(String authorId) {
        log.info("Deleting author with ID: {}", authorId);
        return restClient.delete("/api/v1/authors/" + authorId);
    }
    
    /**
     * Searches for authors by name.
     *
     * @param name The name to search for
     * @return The response containing matching authors
     */
    @Step("Search authors by name: {0}")
    public Response searchAuthorsByName(String name) {
        log.info("Searching for authors with name containing: {}", name);
        return restClient.get("/api/v1/authors/search?name=" + name);
    }
}