package com.bookstore.services;

import com.bookstore.core.api.GraphQLClient;
import com.bookstore.core.config.Environment;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Client for interacting with the Book Service GraphQL API.
 */
@Slf4j
public class BookServiceClient {
    private final GraphQLClient graphQLClient;
    private final String baseUrl;
    
    public BookServiceClient() {
        Environment env = Environment.getInstance();
        this.baseUrl = env.getBookServiceUrl();
        this.graphQLClient = new GraphQLClient(this.baseUrl, env.getBookServiceGraphqlEndpoint());
        log.info("Initialized BookServiceClient with base URL: {}", baseUrl);
    }
    
    /**
     * Creates a new book.
     *
     * @param name The book name
     * @param description The book description
     * @param price The book price
     * @param authorId The author ID
     * @param skuCode The SKU code
     * @return The response containing the created book
     */
    @Step("Create book with name: {0}")
    public Response createBook(String name, String description, BigDecimal price, String authorId, String skuCode) {
        String mutation = "mutation CreateBook($input: BookInput!) { createBook(bookInput: $input) { id name description price authorId skuCode } }";
        
        Map<String, Object> variables = new HashMap<>();
        Map<String, Object> input = new HashMap<>();
        input.put("name", name);
        input.put("description", description);
        input.put("price", price);
        input.put("authorId", authorId);
        input.put("skuCode", skuCode);
        variables.put("input", input);
        
        log.info("Creating book with name: {}", name);
        return graphQLClient.mutation("createBook", mutation, variables).getRawResponse();
    }
    
    /**
     * Gets a book by ID.
     *
     * @param bookId The book ID
     * @return The response containing the book
     */
    @Step("Get book by ID: {0}")
    public Response getBookById(String bookId) {
        String query = "query GetBook($id: ID!) { book(id: $id) { id name description price authorId skuCode } }";
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", bookId);
        
        log.info("Getting book with ID: {}", bookId);
        return graphQLClient.query("getBookById", query, variables).getRawResponse();
    }
    
    /**
     * Gets all books.
     *
     * @return The response containing all books
     */
    @Step("Get all books")
    public Response getAllBooks() {
        String query = "query { allBooks { id name description price authorId skuCode } }";
        
        log.info("Getting all books");
        return graphQLClient.query("getAllBooks", query).getRawResponse();
    }
    
    /**
     * Gets books by author ID.
     *
     * @param authorId The author ID
     * @return The response containing books by the author
     */
    @Step("Get books by author ID: {0}")
    public Response getBooksByAuthorId(String authorId) {
        String query = "query GetBooksByAuthor($authorId: ID!) { booksByAuthor(authorId: $authorId) { id name description price authorId skuCode } }";
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("authorId", authorId);
        
        log.info("Getting books for author with ID: {}", authorId);
        return graphQLClient.query("getBooksByAuthor", query, variables).getRawResponse();
    }
    
    /**
     * Updates a book.
     *
     * @param bookId The book ID
     * @param updates Map of fields to update and their new values
     * @return The response containing the updated book
     */
    @Step("Update book with ID: {0}")
    public Response updateBook(String bookId, Map<String, Object> updates) {
        String mutation = "mutation UpdateBook($id: ID!, $input: BookUpdateInput!) { updateBook(id: $id, bookInput: $input) { id name description price authorId skuCode } }";
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", bookId);
        variables.put("input", updates);
        
        log.info("Updating book with ID: {}", bookId);
        return graphQLClient.mutation("updateBook", mutation, variables).getRawResponse();
    }
    
    /**
     * Deletes a book.
     *
     * @param bookId The book ID
     * @return The response from the delete operation
     */
    @Step("Delete book with ID: {0}")
    public Response deleteBook(String bookId) {
        String mutation = "mutation DeleteBook($id: ID!) { deleteBook(id: $id) }";
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("id", bookId);
        
        log.info("Deleting book with ID: {}", bookId);
        return graphQLClient.mutation("deleteBook", mutation, variables).getRawResponse();
    }
    
    /**
     * Searches for books by name.
     *
     * @param name The name to search for
     * @return The response containing matching books
     */
    @Step("Search books by name: {0}")
    public Response searchBooksByName(String name) {
        String query = "query SearchBooks($name: String!) { searchBooks(name: $name) { id name description price authorId skuCode } }";
        
        Map<String, Object> variables = new HashMap<>();
        variables.put("name", name);
        
        log.info("Searching for books with name containing: {}", name);
        return graphQLClient.query("searchBooksByName", query, variables).getRawResponse();
    }
}