package com.bookstore.core.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.response.Response;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

/**
 * Client for making GraphQL API calls.
 * Supports both queries and mutations with variables.
 */
@Slf4j
public class GraphQLClient {
    private final RestClient restClient;
    private final String graphqlEndpoint;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Creates a new GraphQLClient with the specified base URL and GraphQL endpoint.
     *
     * @param baseUrl The base URL of the GraphQL service
     * @param graphqlEndpoint The GraphQL endpoint path
     */
    public GraphQLClient(String baseUrl, String graphqlEndpoint) {
        this.restClient = new RestClient(baseUrl);
        this.graphqlEndpoint = graphqlEndpoint;
        log.info("Initialized GraphQL client for endpoint: {}{}", baseUrl, graphqlEndpoint);
    }
    
    /**
     * Executes a GraphQL query.
     *
     * @param queryName The name of the query for logging purposes
     * @param query The GraphQL query string
     * @return The GraphQL response
     */
    public GraphQLResponse query(String queryName, String query) {
        return query(queryName, query, Collections.emptyMap());
    }
    
    /**
     * Executes a GraphQL query with variables.
     *
     * @param queryName The name of the query for logging purposes
     * @param query The GraphQL query string
     * @param variables Map of query variables
     * @return The GraphQL response
     */
    public GraphQLResponse query(String queryName, String query, Map<String, Object> variables) {
        log.info("Executing GraphQL query: {}", queryName);
        return executeGraphQL(query, variables);
    }
    
    /**
     * Executes a GraphQL mutation.
     *
     * @param mutationName The name of the mutation for logging purposes
     * @param mutation The GraphQL mutation string
     * @return The GraphQL response
     */
    public GraphQLResponse mutation(String mutationName, String mutation) {
        return mutation(mutationName, mutation, Collections.emptyMap());
    }
    
    /**
     * Executes a GraphQL mutation with variables.
     *
     * @param mutationName The name of the mutation for logging purposes
     * @param mutation The GraphQL mutation string
     * @param variables Map of mutation variables
     * @return The GraphQL response
     */
    public GraphQLResponse mutation(String mutationName, String mutation, Map<String, Object> variables) {
        log.info("Executing GraphQL mutation: {}", mutationName);
        return executeGraphQL(mutation, variables);
    }
    
    /**
     * Executes a GraphQL operation (query or mutation).
     *
     * @param query The GraphQL operation string
     * @param variables Map of operation variables
     * @return The GraphQL response
     */
    private GraphQLResponse executeGraphQL(String query, Map<String, Object> variables) {
        try {
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("query", query);
            
            if (!variables.isEmpty()) {
                requestBody.set("variables", objectMapper.valueToTree(variables));
            }
            
            Response response = restClient.post(graphqlEndpoint, requestBody);
            
            JsonNode responseJson = objectMapper.readTree(response.getBody().asString());
            
            boolean hasErrors = responseJson.has("errors") && !responseJson.get("errors").isEmpty();
            JsonNode data = responseJson.has("data") ? responseJson.get("data") : null;
            JsonNode errors = hasErrors ? responseJson.get("errors") : null;
            
            return new GraphQLResponse(response, data, errors, hasErrors);
        } catch (IOException e) {
            log.error("Error parsing GraphQL response", e);
            throw new RuntimeException("Failed to parse GraphQL response", e);
        }
    }
    
    /**
     * Represents a GraphQL response with data and error information.
     */
    @Getter
    @AllArgsConstructor
    public static class GraphQLResponse {
        private final Response rawResponse;
        private final JsonNode data;
        private final JsonNode errors;
        private final boolean hasErrors;
        
        /**
         * Gets the HTTP status code of the response.
         *
         * @return The HTTP status code
         */
        public int getStatusCode() {
            return rawResponse.getStatusCode();
        }
        
        /**
         * Gets data from the response as the specified type.
         *
         * @param <T> The type to convert to
         * @param clazz The class of the type
         * @return The response data as the specified type
         * @throws IOException If there is an error parsing the JSON
         */
        public <T> T getData(Class<T> clazz) throws IOException {
            if (data == null) {
                return null;
            }
            ObjectMapper mapper = new ObjectMapper();
            return mapper.treeToValue(data, clazz);
        }
    }
}