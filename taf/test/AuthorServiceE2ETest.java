package test;

import base.BaseE2ETest;
import client.RestClient;
import config.TestConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Tests for Author Service REST API
 * Tests: A-E2E-001 to A-E2E-006 from test cases document
 */
@Epic("E2E API Tests")
@Feature("Author Service REST")
@Tag("e2e")
@Tag("rest")
class AuthorServiceE2ETest extends BaseE2ETest {
    private static RestClient restClient;
    private static String baseUrl;
    private static final String AUTHORS_ENDPOINT = "/api/authors";
    private static ObjectMapper objectMapper;

    @BeforeAll
    static void setUp() {
        restClient = new RestClient();
        baseUrl = TestConfig.getInstance().getApiGatewayBaseUrl();
        objectMapper = new ObjectMapper();
    }

    @Test
    @Story("Author Retrieval")
    @DisplayName("A-E2E-001: Retrieve all authors")
    @Description("Get all authors from the system via REST API")
    @Severity(SeverityLevel.BLOCKER)
    void testGetAllAuthors() {
        // Given
        String endpoint = baseUrl + AUTHORS_ENDPOINT;
        
        // When
        List<Map<String, Object>> authors = restClient.get(endpoint, new TypeReference<List<Map<String, Object>>>() {});
        
        // Then
        assertNotNull(authors, "Authors list should not be null");
        assertFalse(authors.isEmpty(), "Authors list should not be empty");
        
        // Validate first author structure
        Map<String, Object> firstAuthor = authors.get(0);
        assertTrue(firstAuthor.containsKey("id"), "Author should have id field");
        assertTrue(firstAuthor.containsKey("name"), "Author should have name field");
        assertTrue(firstAuthor.containsKey("birthDate"), "Author should have birthDate field");
        
        logInfo("A-E2E-001: Found {} authors", authors.size());
        logDebug("First author: {}", firstAuthor.get("name"));
    }

    @Test
    @Story("Author Creation")
    @DisplayName("A-E2E-002: Create a new author")
    @Description("Create a new author via REST API")
    @Severity(SeverityLevel.CRITICAL)
    void testCreateAuthor() {
        // Given
        String endpoint = baseUrl + AUTHORS_ENDPOINT;
        Map<String, Object> newAuthor = new HashMap<>();
        newAuthor.put("name", "Test Author E2E");
        newAuthor.put("birthDate", new int[]{1985, 5, 15}); // Following the actual API format [year, month, day]
        
        // When
        Map<String, Object> createdAuthor = restClient.post(endpoint, newAuthor, new TypeReference<Map<String, Object>>() {});
        
        // Then
        assertNotNull(createdAuthor, "Created author should not be null");
        assertTrue(createdAuthor.containsKey("id"), "Created author should have id");
        assertEquals("Test Author E2E", createdAuthor.get("name"), "Author name should match");
        assertNotNull(createdAuthor.get("birthDate"), "Author should have birth date");
        
        logInfo("A-E2E-002: Created author with ID: {}", createdAuthor.get("id"));
    }

    @Test
    @Story("Author Deletion")
    @DisplayName("A-E2E-003: Delete an existing author")
    @Description("Delete an existing author via REST API")
    @Severity(SeverityLevel.NORMAL)
    void testDeleteExistingAuthor() {
        // Given - First create an author to delete
        String endpoint = baseUrl + AUTHORS_ENDPOINT;
        Map<String, Object> newAuthor = new HashMap<>();
        newAuthor.put("name", "Author To Delete");
        newAuthor.put("birthDate", new int[]{1990, 1, 1});
        
        Map<String, Object> createdAuthor = restClient.post(endpoint, newAuthor, new TypeReference<Map<String, Object>>() {});
        String authorId = createdAuthor.get("id").toString();
        
        // When - Delete the author
        String deleteEndpoint = endpoint + "/" + authorId;
        
        try {
            restClient.delete(deleteEndpoint, String.class);
            logInfo("A-E2E-003: Successfully deleted author with ID: {}", authorId);
        } catch (Exception e) {
            // DELETE might return 200 or 204, both are acceptable
            if (e.getMessage().contains("204") || e.getMessage().contains("200")) {
                logInfo("A-E2E-003: Successfully deleted author with ID: {}", authorId);
            } else {
                throw e;
            }
        }
    }

    @Test
    @Story("Author Deletion")
    @DisplayName("A-E2E-004: Delete non-existent author")
    @Description("Attempt to delete an author that doesn't exist")
    @Severity(SeverityLevel.NORMAL)
    void testDeleteNonExistentAuthor() {
        // Given
        String nonExistentId = "999999";
        String deleteEndpoint = baseUrl + AUTHORS_ENDPOINT + "/" + nonExistentId;
        
        // When & Then
        try {
            restClient.delete(deleteEndpoint, String.class);
            // If no exception, deletion was successful (might return 204)
            logInfo("A-E2E-004: Delete operation completed for non-existent author");
        } catch (Exception e) {
            // 404 Not Found or 204 No Content are both acceptable
            assertTrue(e.getMessage().contains("404") || e.getMessage().contains("204") || e.getMessage().contains("200"),
                "Should receive appropriate response for non-existent author deletion");
            logInfo("A-E2E-004: Correctly handled deletion of non-existent author");
        }
    }

    @Test
    @Story("Author Validation")
    @DisplayName("A-E2E-005: Create author with invalid date format")
    @Description("Attempt to create an author with invalid birth date format")
    @Severity(SeverityLevel.NORMAL)
    void testCreateAuthorWithInvalidDate() {
        // Given
        String endpoint = baseUrl + AUTHORS_ENDPOINT;
        
        // When & Then - Try with string date instead of array
        try {
            Map<String, Object> invalidAuthor = new HashMap<>();
            invalidAuthor.put("name", "Invalid Date Author");
            invalidAuthor.put("birthDate", "invalid-date-format");
            
            restClient.post(endpoint, invalidAuthor, new TypeReference<Map<String, Object>>() {});
            
            fail("Should have thrown validation error for invalid date format");
        } catch (Exception e) {
            // Should receive 400 Bad Request for validation error
            assertTrue(e.getMessage().contains("400") || e.getMessage().contains("Bad Request"),
                "Should receive validation error for invalid date format");
            logInfo("A-E2E-005: Correctly rejected invalid date format");
        }
    }

    @Test
    @Story("Author Validation")
    @DisplayName("A-E2E-006: Create author with future birth date - STRICT")
    @Description("Attempt to create an author with future birth date - system MUST reject this")
    @Severity(SeverityLevel.CRITICAL)
    void testCreateAuthorWithFutureDate() {
        // Given
        String endpoint = baseUrl + AUTHORS_ENDPOINT;
        Map<String, Object> futureAuthor = new HashMap<>();
        futureAuthor.put("name", "Future Date Author");
        futureAuthor.put("birthDate", new int[]{2050, 1, 1}); // Future date
        
        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            restClient.post(endpoint, futureAuthor, new TypeReference<Map<String, Object>>() {});
        }, "System MUST reject authors with future birth dates");
        
        // Verify it's a validation error
        assertTrue(exception.getMessage().contains("400") || exception.getMessage().contains("Bad Request"),
            "Should receive HTTP 400 Bad Request for invalid future birth date");
        logInfo("A-E2E-006: STRICT validation correctly rejected future birth date");
    }

    @Test
    @Story("Author Validation")
    @DisplayName("A-E2E-007: Create author with null birth date - STRICT")
    @Description("Attempt to create an author with null birth date - system MUST reject this")
    @Severity(SeverityLevel.CRITICAL)
    void testCreateAuthorWithNullBirthDate() {
        // Given
        String endpoint = baseUrl + AUTHORS_ENDPOINT;
        Map<String, Object> invalidAuthor = new HashMap<>();
        invalidAuthor.put("name", "Author Without Birth Date");
        invalidAuthor.put("birthDate", null); // Null birth date
        
        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            restClient.post(endpoint, invalidAuthor, new TypeReference<Map<String, Object>>() {});
        }, "System MUST reject authors with null birth dates");
        
        // Verify it's a validation error
        assertTrue(exception.getMessage().contains("400") || exception.getMessage().contains("Bad Request"),
            "Should receive HTTP 400 Bad Request for null birth date");
        logInfo("A-E2E-007: STRICT validation correctly rejected null birth date");
    }

    @Test
    @Story("Author Validation")
    @DisplayName("A-E2E-008: Create author with empty name - STRICT")
    @Description("Attempt to create an author with empty name - system MUST reject this")
    @Severity(SeverityLevel.CRITICAL)
    void testCreateAuthorWithEmptyName() {
        // Given
        String endpoint = baseUrl + AUTHORS_ENDPOINT;
        Map<String, Object> invalidAuthor = new HashMap<>();
        invalidAuthor.put("name", ""); // Empty name
        invalidAuthor.put("birthDate", new int[]{1980, 1, 1});
        
        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            restClient.post(endpoint, invalidAuthor, new TypeReference<Map<String, Object>>() {});
        }, "System MUST reject authors with empty names");
        
        // Verify it's a validation error
        assertTrue(exception.getMessage().contains("400") || exception.getMessage().contains("Bad Request"),
            "Should receive HTTP 400 Bad Request for empty name");
        logInfo("A-E2E-008: STRICT validation correctly rejected empty name");
    }
}

