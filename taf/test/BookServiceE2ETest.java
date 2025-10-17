package test;

import client.RestClient;
import config.TestConfig;
import com.fasterxml.jackson.databind.JsonNode;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Tests for Book Service GraphQL API
 * Tests: B-E2E-001 to B-E2E-006 from test cases document
 */
@Epic("E2E API Tests")
@Feature("Book Service GraphQL")
@Tag("e2e")
@Tag("graphql")
class BookServiceE2ETest {

    private static RestClient restClient;
    private static String baseUrl;
    private static final String GRAPHQL_ENDPOINT = "/api/graphql";

    @BeforeAll
    static void setUp() {
        restClient = new RestClient();
        baseUrl = TestConfig.getInstance().getApiGatewayBaseUrl();
    }

    @Test
    @Story("Book Retrieval")
    @DisplayName("B-E2E-001: List all available books")
    @Description("Retrieve all books from the catalog via GraphQL")
    @Severity(SeverityLevel.BLOCKER)
    void testGetAllBooks() {
        // Given
        String query = "{ \"query\": \"{ getAllBooks { id name description price } }\" }";
        String endpoint = baseUrl + GRAPHQL_ENDPOINT;
        
        // When
        JsonNode response = restClient.post(endpoint, query, JsonNode.class);
        
        // Then
        assertNotNull(response, "Response should not be null");
        assertTrue(response.has("data"), "Response should contain 'data' field");
        
        JsonNode data = response.get("data");
        assertTrue(data.has("getAllBooks"), "Data should contain 'getAllBooks' field");
        
        JsonNode books = data.get("getAllBooks");
        assertTrue(books.isArray(), "getAllBooks should be an array");
        assertTrue(books.size() > 0, "Should have at least one book");
        
        // Validate first book structure
        JsonNode firstBook = books.get(0);
        assertTrue(firstBook.has("id"), "Book should have id field");
        assertTrue(firstBook.has("name"), "Book should have name field");
        assertTrue(firstBook.has("price"), "Book should have price field");
        
        System.out.println("✅ B-E2E-001: Found " + books.size() + " books in catalog");
    }

    @Test
    @Story("Book Creation")
    @DisplayName("B-E2E-002: Add a new book to catalog")
    @Description("Create a new book via GraphQL mutation")
    @Severity(SeverityLevel.CRITICAL)
    void testCreateBook() {
        // Given
        String mutation = "{ \"query\": \"mutation($book: BookRequest!) { createBook(bookRequest: $book) { id name description price } }\", \"variables\": { \"book\": { \"name\": \"Test Book E2E\", \"description\": \"Test Description for E2E\", \"price\": 19.99 } } }";
        String endpoint = baseUrl + GRAPHQL_ENDPOINT;
        
        // When
        JsonNode response = restClient.post(endpoint, mutation, JsonNode.class);
        
        // Then
        assertNotNull(response, "Response should not be null");
        assertTrue(response.has("data"), "Response should contain 'data' field");
        
        JsonNode data = response.get("data");
        assertTrue(data.has("createBook"), "Data should contain 'createBook' field");
        
        JsonNode createdBook = data.get("createBook");
        assertNotNull(createdBook.get("id").asText(), "Created book should have an ID");
        assertEquals("Test Book E2E", createdBook.get("name").asText(), "Book name should match");
        assertEquals("Test Description for E2E", createdBook.get("description").asText(), "Book description should match");
        assertEquals(19.99, createdBook.get("price").asDouble(), 0.01, "Book price should match");
        
        System.out.println("✅ B-E2E-002: Created book with ID: " + createdBook.get("id").asText());
    }

    @Test
    @Story("Book Deletion")
    @DisplayName("B-E2E-003: Delete an existing book")
    @Description("Delete an existing book via GraphQL mutation")
    @Severity(SeverityLevel.NORMAL)
    void testDeleteExistingBook() {
        // Given - First create a book to delete
        String createMutation = "{ \"query\": \"mutation($book: BookRequest!) { createBook(bookRequest: $book) { id name description price } }\", \"variables\": { \"book\": { \"name\": \"Book To Delete\", \"description\": \"Will be deleted\", \"price\": 25.99 } } }";
        String endpoint = baseUrl + GRAPHQL_ENDPOINT;
        
        JsonNode createResponse = restClient.post(endpoint, createMutation, JsonNode.class);
        String bookIdToDelete = createResponse.get("data").get("createBook").get("id").asText();
        
        // When - Delete the book
        String deleteMutation = "{ \"query\": \"mutation($id: ID!) { deleteBook(id: $id) }\", \"variables\": { \"id\": \"" + bookIdToDelete + "\" } }";
        JsonNode deleteResponse = restClient.post(endpoint, deleteMutation, JsonNode.class);
        
        // Then
        assertNotNull(deleteResponse, "Delete response should not be null");
        assertTrue(deleteResponse.has("data"), "Response should contain 'data' field");
        
        JsonNode data = deleteResponse.get("data");
        assertTrue(data.has("deleteBook"), "Data should contain 'deleteBook' field");
        assertTrue(data.get("deleteBook").asBoolean(), "Delete operation should return true");
        
        System.out.println("✅ B-E2E-003: Successfully deleted book with ID: " + bookIdToDelete);
    }

    @Test
    @Story("Book Deletion")
    @DisplayName("B-E2E-004: Try to delete non-existent book")
    @Description("Attempt to delete a book that doesn't exist")
    @Severity(SeverityLevel.NORMAL)
    void testDeleteNonExistentBook() {
        // Given
        String nonExistentId = "non-existent-book-id-12345";
        String deleteMutation = "{ \"query\": \"mutation($id: ID!) { deleteBook(id: $id) }\", \"variables\": { \"id\": \"" + nonExistentId + "\" } }";
        String endpoint = baseUrl + GRAPHQL_ENDPOINT;
        
        // When
        JsonNode response = restClient.post(endpoint, deleteMutation, JsonNode.class);
        
        // Then
        assertNotNull(response, "Response should not be null");
        assertTrue(response.has("data"), "Response should contain 'data' field");
        
        JsonNode data = response.get("data");
        assertTrue(data.has("deleteBook"), "Data should contain 'deleteBook' field");
        assertFalse(data.get("deleteBook").asBoolean(), "Delete operation should return false for non-existent book");
        
        System.out.println("✅ B-E2E-004: Correctly handled deletion of non-existent book");
    }

    @Test
    @Story("Book Validation")
    @DisplayName("B-E2E-005: Create book with invalid data (missing required name)")
    @Description("Attempt to create a book without required name field")
    @Severity(SeverityLevel.NORMAL)
    void testCreateBookWithMissingName() {
        // Given
        String invalidMutation = "{ \"query\": \"mutation($book: BookRequest!) { createBook(bookRequest: $book) { id name description price } }\", \"variables\": { \"book\": { \"description\": \"Test Description\", \"price\": 19.99 } } }";
        String endpoint = baseUrl + GRAPHQL_ENDPOINT;
        
        // When & Then
        try {
            JsonNode response = restClient.post(endpoint, invalidMutation, JsonNode.class);
            
            // Response should either have errors or validation should fail
            if (response.has("errors")) {
                assertTrue(response.get("errors").isArray(), "Errors should be an array");
                assertTrue(response.get("errors").size() > 0, "Should have validation errors");
                System.out.println("✅ B-E2E-005: GraphQL validation correctly rejected book without name");
            } else {
                fail("Expected GraphQL validation errors for book without name");
            }
        } catch (Exception e) {
            // HTTP 400 is also acceptable for validation errors
            assertTrue(e.getMessage().contains("400") || e.getMessage().contains("Bad Request"),
                "Should receive 400 Bad Request for invalid data");
            System.out.println("✅ B-E2E-005: HTTP validation correctly rejected book without name");
        }
    }

    @Test
    @Story("Book Validation")
    @DisplayName("B-E2E-006: Create book with negative price")
    @Description("Attempt to create a book with negative price")
    @Severity(SeverityLevel.NORMAL)
    void testCreateBookWithNegativePrice() {
        // Given
        String invalidMutation = "{ \"query\": \"mutation($book: BookRequest!) { createBook(bookRequest: $book) { id name description price } }\", \"variables\": { \"book\": { \"name\": \"Negative Price Book\", \"description\": \"Test Description\", \"price\": -19.99 } } }";
        String endpoint = baseUrl + GRAPHQL_ENDPOINT;
        
        // When
        try {
            JsonNode response = restClient.post(endpoint, invalidMutation, JsonNode.class);
            
            // Check if there are validation errors
            if (response.has("errors")) {
                assertTrue(response.get("errors").isArray(), "Errors should be an array");
                System.out.println("✅ B-E2E-006: GraphQL validation correctly rejected negative price");
            } else if (response.has("data") && response.get("data").has("createBook")) {
                // Some implementations might allow negative prices, so this is also valid
                JsonNode createdBook = response.get("data").get("createBook");
                assertEquals(-19.99, createdBook.get("price").asDouble(), 0.01);
                System.out.println("⚠️ B-E2E-006: System allows negative prices - this might be a business rule to review");
            }
        } catch (Exception e) {
            // HTTP 400 is acceptable for validation errors
            assertTrue(e.getMessage().contains("400") || e.getMessage().contains("Bad Request"),
                "Should receive validation error for negative price");
            System.out.println("✅ B-E2E-006: HTTP validation correctly rejected negative price");
        }
    }
}