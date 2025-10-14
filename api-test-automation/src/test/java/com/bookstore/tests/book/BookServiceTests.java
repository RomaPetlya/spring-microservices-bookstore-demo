package com.bookstore.tests.book;

import com.bookstore.core.data.DataGenerator;
import com.bookstore.core.utils.AssertionUtils;
import com.bookstore.services.AuthorServiceClient;
import com.bookstore.services.BookServiceClient;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Epic("Book Service API Tests")
@Feature("Book Management")
public class BookServiceTests {
    private BookServiceClient bookServiceClient;
    private AuthorServiceClient authorServiceClient;
    private String testBookId;
    private String testAuthorId;
    
    @BeforeClass
    public void setup() {
        bookServiceClient = new BookServiceClient();
        authorServiceClient = new AuthorServiceClient();
        
        // Create a test author to use in book tests
        String name = DataGenerator.generateAuthorName();
        String bio = DataGenerator.generateAuthorBio();
        String email = DataGenerator.generateEmail();
        
        Response authorResponse = authorServiceClient.createAuthor(name, bio, email);
        JSONObject authorJson = new JSONObject(authorResponse.getBody().asString());
        testAuthorId = authorJson.getString("id");
    }
    
    @Test(priority = 1)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Create a new book")
    @Story("Create Book")
    public void testCreateBook() {
        String name = DataGenerator.generateBookName();
        String description = DataGenerator.generateBookDescription();
        BigDecimal price = DataGenerator.generateBookPrice();
        String skuCode = DataGenerator.generateSkuCode();
        
        Response response = bookServiceClient.createBook(name, description, price, testAuthorId, skuCode);
        
        AssertionUtils.assertStatusCode(response, 200);
        JSONObject jsonResponse = new JSONObject(response.getBody().asString());
        JSONObject data = jsonResponse.getJSONObject("data");
        JSONObject createBook = data.getJSONObject("createBook");
        
        AssertionUtils.assertNotNull(createBook.getString("id"), "Book ID should not be null");
        AssertionUtils.assertJsonValue(createBook, "name", name);
        AssertionUtils.assertJsonValue(createBook, "description", description);
        AssertionUtils.assertJsonValue(createBook, "authorId", testAuthorId);
        AssertionUtils.assertJsonValue(createBook, "skuCode", skuCode);
        
        // Store book ID for future tests
        testBookId = createBook.getString("id");
    }
    
    @Test(priority = 2, dependsOnMethods = "testCreateBook")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Get book by ID")
    @Story("Retrieve Book")
    public void testGetBookById() {
        Response response = bookServiceClient.getBookById(testBookId);
        
        AssertionUtils.assertStatusCode(response, 200);
        JSONObject jsonResponse = new JSONObject(response.getBody().asString());
        JSONObject data = jsonResponse.getJSONObject("data");
        JSONObject book = data.getJSONObject("book");
        
        AssertionUtils.assertJsonValue(book, "id", testBookId);
        AssertionUtils.assertNotNull(book.getString("name"), "Book name should not be null");
    }
    
    @Test(priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Get all books")
    @Story("Retrieve Books")
    public void testGetAllBooks() {
        Response response = bookServiceClient.getAllBooks();
        
        AssertionUtils.assertStatusCode(response, 200);
        JSONObject jsonResponse = new JSONObject(response.getBody().asString());
        JSONObject data = jsonResponse.getJSONObject("data");
        AssertionUtils.assertJsonArrayNotEmpty(data, "allBooks");
    }
    
    @Test(priority = 4, dependsOnMethods = "testCreateBook")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Get books by author ID")
    @Story("Retrieve Books by Author")
    public void testGetBooksByAuthorId() {
        Response response = bookServiceClient.getBooksByAuthorId(testAuthorId);
        
        AssertionUtils.assertStatusCode(response, 200);
        JSONObject jsonResponse = new JSONObject(response.getBody().asString());
        JSONObject data = jsonResponse.getJSONObject("data");
        AssertionUtils.assertJsonArrayNotEmpty(data, "booksByAuthor");
    }
    
    @Test(priority = 5, dependsOnMethods = "testCreateBook")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Update a book")
    @Story("Update Book")
    public void testUpdateBook() {
        String updatedDescription = DataGenerator.generateBookDescription();
        Map<String, Object> updates = new HashMap<>();
        updates.put("description", updatedDescription);
        
        Response response = bookServiceClient.updateBook(testBookId, updates);
        
        AssertionUtils.assertStatusCode(response, 200);
        JSONObject jsonResponse = new JSONObject(response.getBody().asString());
        JSONObject data = jsonResponse.getJSONObject("data");
        JSONObject updateBook = data.getJSONObject("updateBook");
        
        AssertionUtils.assertJsonValue(updateBook, "id", testBookId);
        AssertionUtils.assertJsonValue(updateBook, "description", updatedDescription);
    }
    
    @Test(priority = 6)
    @Severity(SeverityLevel.NORMAL)
    @Description("Search books by name")
    @Story("Search Books")
    public void testSearchBooksByName() {
        // Create a new book with a unique name for testing search
        String uniqueName = "UniqueBook" + System.currentTimeMillis();
        String description = DataGenerator.generateBookDescription();
        BigDecimal price = DataGenerator.generateBookPrice();
        String skuCode = DataGenerator.generateSkuCode();
        
        bookServiceClient.createBook(uniqueName, description, price, testAuthorId, skuCode);
        
        // Search for the book
        Response response = bookServiceClient.searchBooksByName(uniqueName);
        
        AssertionUtils.assertStatusCode(response, 200);
        JSONObject jsonResponse = new JSONObject(response.getBody().asString());
        JSONObject data = jsonResponse.getJSONObject("data");
        AssertionUtils.assertJsonArrayNotEmpty(data, "searchBooks");
        
        // Verify the search results contain the book with the unique name
        String responseBody = response.getBody().asString();
        AssertionUtils.assertContains(responseBody, uniqueName);
    }
    
    @Test(priority = 7, dependsOnMethods = "testGetBookById")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Delete a book")
    @Story("Delete Book")
    public void testDeleteBook() {
        // Create a new book to delete
        String name = DataGenerator.generateBookName();
        String description = DataGenerator.generateBookDescription();
        BigDecimal price = DataGenerator.generateBookPrice();
        String skuCode = DataGenerator.generateSkuCode();
        
        Response createResponse = bookServiceClient.createBook(name, description, price, testAuthorId, skuCode);
        JSONObject createJson = new JSONObject(createResponse.getBody().asString());
        JSONObject data = createJson.getJSONObject("data");
        String bookIdToDelete = data.getJSONObject("createBook").getString("id");
        
        // Delete the book
        Response deleteResponse = bookServiceClient.deleteBook(bookIdToDelete);
        AssertionUtils.assertStatusCode(deleteResponse, 200);
        JSONObject deleteJson = new JSONObject(deleteResponse.getBody().asString());
        JSONObject deleteData = deleteJson.getJSONObject("data");
        AssertionUtils.assertTrue(deleteData.getBoolean("deleteBook"), "Delete operation should return true");
        
        // Verify the book no longer exists
        Response getResponse = bookServiceClient.getBookById(bookIdToDelete);
        JSONObject getJson = new JSONObject(getResponse.getBody().asString());
        AssertionUtils.assertNull(getJson.getJSONObject("data").optJSONObject("book"), "Book should be null after deletion");
    }
}