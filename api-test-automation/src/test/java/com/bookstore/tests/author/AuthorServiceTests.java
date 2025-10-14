package com.bookstore.tests.author;

import com.bookstore.core.data.DataGenerator;
import com.bookstore.core.utils.AssertionUtils;
import com.bookstore.services.AuthorServiceClient;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

@Epic("Author Service API Tests")
@Feature("Author Management")
public class AuthorServiceTests {
    private AuthorServiceClient authorServiceClient;
    private String testAuthorId;
    
    @BeforeClass
    public void setup() {
        authorServiceClient = new AuthorServiceClient();
    }
    
    @Test(priority = 1)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Create a new author")
    @Story("Create Author")
    public void testCreateAuthor() {
        String name = DataGenerator.generateAuthorName();
        String bio = DataGenerator.generateAuthorBio();
        String email = DataGenerator.generateEmail();
        
        Response response = authorServiceClient.createAuthor(name, bio, email);
        
        AssertionUtils.assertStatusCode(response, 201);
        JSONObject jsonResponse = new JSONObject(response.getBody().asString());
        AssertionUtils.assertJsonValue(jsonResponse, "name", name);
        AssertionUtils.assertJsonValue(jsonResponse, "bio", bio);
        AssertionUtils.assertJsonValue(jsonResponse, "email", email);
        AssertionUtils.assertNotNull(jsonResponse.getString("id"), "Author ID should not be null");
        
        // Store author ID for future tests
        testAuthorId = jsonResponse.getString("id");
    }
    
    @Test(priority = 2, dependsOnMethods = "testCreateAuthor")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Get author by ID")
    @Story("Retrieve Author")
    public void testGetAuthorById() {
        Response response = authorServiceClient.getAuthorById(testAuthorId);
        
        AssertionUtils.assertStatusCode(response, 200);
        JSONObject jsonResponse = new JSONObject(response.getBody().asString());
        AssertionUtils.assertJsonValue(jsonResponse, "id", testAuthorId);
        AssertionUtils.assertNotNull(jsonResponse.getString("name"), "Author name should not be null");
    }
    
    @Test(priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Get all authors")
    @Story("Retrieve Authors")
    public void testGetAllAuthors() {
        Response response = authorServiceClient.getAllAuthors();
        
        AssertionUtils.assertStatusCode(response, 200);
        AssertionUtils.assertJsonArray(response);
    }
    
    @Test(priority = 4, dependsOnMethods = "testCreateAuthor")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Update an author")
    @Story("Update Author")
    public void testUpdateAuthor() {
        String updatedBio = DataGenerator.generateAuthorBio();
        Map<String, Object> updates = new HashMap<>();
        updates.put("bio", updatedBio);
        
        Response response = authorServiceClient.updateAuthor(testAuthorId, updates);
        
        AssertionUtils.assertStatusCode(response, 200);
        JSONObject jsonResponse = new JSONObject(response.getBody().asString());
        AssertionUtils.assertJsonValue(jsonResponse, "id", testAuthorId);
        AssertionUtils.assertJsonValue(jsonResponse, "bio", updatedBio);
    }
    
    @Test(priority = 5)
    @Severity(SeverityLevel.NORMAL)
    @Description("Search authors by name")
    @Story("Search Authors")
    public void testSearchAuthorsByName() {
        // Create a new author with a unique name for testing search
        String uniqueName = "UniqueAuthor" + System.currentTimeMillis();
        String bio = DataGenerator.generateAuthorBio();
        String email = DataGenerator.generateEmail();
        
        authorServiceClient.createAuthor(uniqueName, bio, email);
        
        // Search for the author
        Response response = authorServiceClient.searchAuthorsByName(uniqueName);
        
        AssertionUtils.assertStatusCode(response, 200);
        AssertionUtils.assertJsonArray(response);
        
        // Verify the search results contain the author with the unique name
        String responseBody = response.getBody().asString();
        AssertionUtils.assertContains(responseBody, uniqueName);
    }
    
    @Test(priority = 6, dependsOnMethods = "testGetAuthorById")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Delete an author")
    @Story("Delete Author")
    public void testDeleteAuthor() {
        // Create a new author to delete
        String name = DataGenerator.generateAuthorName();
        String bio = DataGenerator.generateAuthorBio();
        String email = DataGenerator.generateEmail();
        
        Response createResponse = authorServiceClient.createAuthor(name, bio, email);
        JSONObject jsonResponse = new JSONObject(createResponse.getBody().asString());
        String authorIdToDelete = jsonResponse.getString("id");
        
        // Delete the author
        Response deleteResponse = authorServiceClient.deleteAuthor(authorIdToDelete);
        AssertionUtils.assertStatusCode(deleteResponse, 204);
        
        // Verify the author no longer exists
        Response getResponse = authorServiceClient.getAuthorById(authorIdToDelete);
        AssertionUtils.assertStatusCode(getResponse, 404);
    }
}