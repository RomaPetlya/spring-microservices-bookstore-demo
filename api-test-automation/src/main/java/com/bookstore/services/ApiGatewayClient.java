package com.bookstore.services;

import com.bookstore.core.api.RestClient;
import com.bookstore.dto.AuthorDto;
import com.bookstore.dto.BookDto;
import com.bookstore.dto.OrderDto;
import com.bookstore.dto.OrderItemDto;
import com.bookstore.dto.StockDto;
import com.fasterxml.jackson.core.type.TypeReference;

import io.restassured.response.Response;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * ApiGatewayClient provides all API operations through API Gateway
 */
public class ApiGatewayClient {
    private final RestClient restClient;
    private final String apiGatewayUrl;

    public ApiGatewayClient(String apiGatewayUrl) {
        this.apiGatewayUrl = apiGatewayUrl;
        this.restClient = new RestClient(apiGatewayUrl);
    }

    // Author Service API through Gateway
    public AuthorDto createAuthor(String name, String bio) {
        AuthorDto authorDto = new AuthorDto();
        authorDto.setName(name);
        authorDto.setBio(bio);

        Response response = restClient.post("/api/authors", authorDto);
        return response.as(AuthorDto.class);
    }

    public List<AuthorDto> getAllAuthors() {
        Response response = restClient.get("/api/authors");
        return response.as(new TypeReference<List<AuthorDto>>() {}.getType());
    }

    public AuthorDto getAuthorById(String id) {
        Response response = restClient.get("/api/authors/" + id);
        return response.as(AuthorDto.class);
    }

    public List<AuthorDto> searchAuthorsByName(String nameQuery) {
        Response response = restClient.get("/api/authors/search?name=" + nameQuery);
        return response.as(new TypeReference<List<AuthorDto>>() {}.getType());
    }

    public void deleteAuthor(String id) {
        restClient.delete("/api/authors/" + id);
    }

    // Book Service API through Gateway (GraphQL)
    public BookDto createBook(String title, String authorId, int quantityAvailable, double price) {
        String mutation = String.format(
            "mutation { saveBook(bookDto: {title: \"%s\", authorId: \"%s\", quantityAvailable: %d, price: %.2f}) { " +
            "id title authorId quantityAvailable price } }",
            title, authorId, quantityAvailable, price
        );

        JSONObject requestBody = new JSONObject();
        requestBody.put("query", mutation);

        Response response = restClient.post("/api/graphql", requestBody.toString());
        
        // Extract book from GraphQL response
        JSONObject responseJson = new JSONObject(response.asString());
        JSONObject data = responseJson.getJSONObject("data");
        JSONObject book = data.getJSONObject("saveBook");
        
        BookDto bookDto = new BookDto();
        bookDto.setId(book.getString("id"));
        bookDto.setTitle(book.getString("title"));
        bookDto.setAuthorId(book.getString("authorId"));
        bookDto.setQuantityAvailable(book.getInt("quantityAvailable"));
        bookDto.setPrice((float)book.getDouble("price"));
        
        return bookDto;
    }

    public List<BookDto> getAllBooks() {
        String query = "{ books { id title authorId quantityAvailable price } }";
        
        JSONObject requestBody = new JSONObject();
        requestBody.put("query", query);

        Response response = restClient.post("/api/graphql", requestBody.toString());
        
        // Parse GraphQL response
        // This is simplified - in real implementation you'd need proper JSON parsing
        return null; // Implement proper parsing
    }

    // Order Service API through Gateway
    public OrderDto createOrder(String userId, List<OrderItemDto> orderItems) {
        OrderDto orderDto = new OrderDto();
        orderDto.setUserId(userId);
        orderDto.setOrderItems(orderItems);

        Response response = restClient.post("/api/order", orderDto);
        return response.as(OrderDto.class);
    }

    // End-to-end tests can be implemented with these methods
    public OrderDto placeOrder(String authorName, String bookTitle, int quantity) {
        // 1. Create author
        AuthorDto author = createAuthor(authorName, "Bio for " + authorName);
        
        // 2. Create book
        BookDto book = createBook(bookTitle, author.getId(), quantity + 10, 29.99);
        
        // 3. Create order
        OrderItemDto orderItem = new OrderItemDto();
        orderItem.setBookId(book.getId());
        orderItem.setQuantity(quantity);
        
        return createOrder(UUID.randomUUID().toString(), List.of(orderItem));
    }
}