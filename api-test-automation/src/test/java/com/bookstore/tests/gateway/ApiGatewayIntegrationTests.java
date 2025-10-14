package com.bookstore.tests.gateway;

import com.bookstore.core.config.TestProperties;
import com.bookstore.dto.AuthorDto;
import com.bookstore.dto.BookDto;
import com.bookstore.dto.OrderDto;
import com.bookstore.dto.OrderItemDto;
import com.bookstore.services.ApiGatewayClient;
import org.apache.commons.lang3.RandomStringUtils;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;
import java.util.UUID;

import static org.testng.Assert.*;

public class ApiGatewayIntegrationTests {

    private ApiGatewayClient apiGatewayClient;

    @BeforeClass
    public void setup() {
        String apiGatewayUrl = TestProperties.getInstance().getApiGatewayUrl();
        apiGatewayClient = new ApiGatewayClient(apiGatewayUrl);
    }

    @Test
    public void testCreateAndGetAuthor() {
        // Generate random author data
        String authorName = "Test Author " + RandomStringUtils.randomAlphabetic(5);
        String authorBio = "Test Bio " + RandomStringUtils.randomAlphabetic(10);
        
        // Create author
        AuthorDto createdAuthor = apiGatewayClient.createAuthor(authorName, authorBio);
        
        // Verify author was created
        assertNotNull(createdAuthor);
        assertNotNull(createdAuthor.getId());
        assertEquals(createdAuthor.getName(), authorName);
        assertEquals(createdAuthor.getBio(), authorBio);
        
        // Get all authors and verify the created author is in the list
        List<AuthorDto> allAuthors = apiGatewayClient.getAllAuthors();
        boolean found = allAuthors.stream()
            .anyMatch(author -> author.getId().equals(createdAuthor.getId()));
        assertTrue(found, "Created author was not found in the list of all authors");
        
        // Get author by ID
        AuthorDto retrievedAuthor = apiGatewayClient.getAuthorById(createdAuthor.getId());
        assertEquals(retrievedAuthor.getId(), createdAuthor.getId());
        assertEquals(retrievedAuthor.getName(), authorName);
        
        // Search for author by name
        List<AuthorDto> searchResults = apiGatewayClient.searchAuthorsByName(authorName.substring(0, 5));
        boolean foundInSearch = searchResults.stream()
            .anyMatch(author -> author.getId().equals(createdAuthor.getId()));
        assertTrue(foundInSearch, "Created author was not found in search results");
        
        // Clean up - delete author
        apiGatewayClient.deleteAuthor(createdAuthor.getId());
    }
    
    @Test
    public void testCreateBook() {
        // First create an author
        String authorName = "Book Author " + RandomStringUtils.randomAlphabetic(5);
        AuthorDto author = apiGatewayClient.createAuthor(authorName, "Bio for book test");
        
        // Create a book
        String bookTitle = "Test Book " + RandomStringUtils.randomAlphabetic(5);
        int quantity = 10;
        double price = 29.99;
        
        BookDto createdBook = apiGatewayClient.createBook(bookTitle, author.getId(), quantity, price);
        
        // Verify book was created
        assertNotNull(createdBook);
        assertNotNull(createdBook.getId());
        assertEquals(createdBook.getTitle(), bookTitle);
        assertEquals(createdBook.getAuthorId(), author.getId());
        assertEquals(createdBook.getQuantityAvailable(), quantity);
        assertEquals(createdBook.getPrice(), price, 0.01);
        
        // Clean up
        apiGatewayClient.deleteAuthor(author.getId());
    }
    
    @Test
    public void testEndToEndOrderFlow() {
        // Create author
        String authorName = "E2E Author " + RandomStringUtils.randomAlphabetic(5);
        String bookTitle = "E2E Book " + RandomStringUtils.randomAlphabetic(5);
        int quantity = 2;
        
        // Place order through the end-to-end flow
        OrderDto order = apiGatewayClient.placeOrder(authorName, bookTitle, quantity);
        
        // Verify order was created
        assertNotNull(order);
        assertNotNull(order.getId());
        assertEquals(order.getOrderItems().size(), 1);
        assertEquals(order.getOrderItems().get(0).getQuantity(), quantity);
    }
}