package com.bookstore.tests.integration;

import com.bookstore.core.data.DataGenerator;
import com.bookstore.core.utils.AssertionUtils;
import com.bookstore.services.AuthorServiceClient;
import com.bookstore.services.BookServiceClient;
import com.bookstore.services.OrderServiceClient;
import com.bookstore.services.StockCheckServiceClient;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Epic("Integration Tests")
@Feature("End-to-End Flows")
public class EndToEndIntegrationTests {
    private AuthorServiceClient authorServiceClient;
    private BookServiceClient bookServiceClient;
    private StockCheckServiceClient stockCheckServiceClient;
    private OrderServiceClient orderServiceClient;
    
    private String authorId;
    private String bookId;
    private String skuCode;
    private String orderId;
    
    @BeforeClass
    public void setup() {
        authorServiceClient = new AuthorServiceClient();
        bookServiceClient = new BookServiceClient();
        stockCheckServiceClient = new StockCheckServiceClient();
        orderServiceClient = new OrderServiceClient();
    }
    
    @Test(priority = 1)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Create author, book, stock, and order - full end-to-end flow")
    @Story("E2E Order Flow")
    public void testEndToEndOrderFlow() {
        // Step 1: Create an author
        String authorName = DataGenerator.generateAuthorName();
        String authorBio = DataGenerator.generateAuthorBio();
        String authorEmail = DataGenerator.generateEmail();
        
        Response authorResponse = authorServiceClient.createAuthor(authorName, authorBio, authorEmail);
        AssertionUtils.assertStatusCode(authorResponse, 201);
        JSONObject authorJson = new JSONObject(authorResponse.getBody().asString());
        authorId = authorJson.getString("id");
        
        // Step 2: Create a book by the author
        String bookName = DataGenerator.generateBookName();
        String bookDescription = DataGenerator.generateBookDescription();
        BigDecimal bookPrice = DataGenerator.generateBookPrice();
        skuCode = DataGenerator.generateSkuCode();
        
        Response bookResponse = bookServiceClient.createBook(bookName, bookDescription, bookPrice, authorId, skuCode);
        AssertionUtils.assertStatusCode(bookResponse, 200);
        JSONObject bookJson = new JSONObject(bookResponse.getBody().asString());
        JSONObject bookData = bookJson.getJSONObject("data").getJSONObject("createBook");
        bookId = bookData.getString("id");
        
        // Step 3: Create stock for the book
        int stockQuantity = 20;
        Response stockResponse = stockCheckServiceClient.createStock(skuCode, stockQuantity);
        AssertionUtils.assertStatusCode(stockResponse, 201);
        
        // Step 4: Verify stock is available
        Response checkStockResponse = stockCheckServiceClient.checkStock(skuCode);
        AssertionUtils.assertStatusCode(checkStockResponse, 200);
        JSONObject stockJson = new JSONObject(checkStockResponse.getBody().asString());
        AssertionUtils.assertJsonValue(stockJson, "isInStock", true);
        AssertionUtils.assertJsonValue(stockJson, "quantity", stockQuantity);
        
        // Step 5: Create an order for the book
        String orderNumber = DataGenerator.generateOrderNumber();
        String customerEmail = DataGenerator.generateEmail();
        int orderQuantity = 2;
        
        Map<String, Object> orderItem = new HashMap<>();
        orderItem.put("skuCode", skuCode);
        orderItem.put("price", bookPrice);
        orderItem.put("quantity", orderQuantity);
        
        List<Map<String, Object>> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        
        Response orderResponse = orderServiceClient.createOrder(orderNumber, customerEmail, orderItems);
        AssertionUtils.assertStatusCode(orderResponse, 201);
        JSONObject orderJson = new JSONObject(orderResponse.getBody().asString());
        orderId = orderJson.getString("id");
        
        // Step 6: Verify stock is reduced after order placement
        Response updatedStockResponse = stockCheckServiceClient.checkStock(skuCode);
        AssertionUtils.assertStatusCode(updatedStockResponse, 200);
        JSONObject updatedStockJson = new JSONObject(updatedStockResponse.getBody().asString());
        AssertionUtils.assertJsonValue(updatedStockJson, "quantity", stockQuantity - orderQuantity);
        
        // Step 7: Update order status to COMPLETED
        Response updateOrderResponse = orderServiceClient.updateOrderStatus(orderId, "COMPLETED");
        AssertionUtils.assertStatusCode(updateOrderResponse, 200);
        JSONObject updatedOrderJson = new JSONObject(updateOrderResponse.getBody().asString());
        AssertionUtils.assertJsonValue(updatedOrderJson, "orderStatus", "COMPLETED");
    }
    
    @Test(priority = 2)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test order cancellation flow with stock reservation and release")
    @Story("E2E Cancellation Flow")
    public void testOrderCancellationFlow() {
        // Step 1: Create a new book with stock
        String authorName = DataGenerator.generateAuthorName();
        String authorBio = DataGenerator.generateAuthorBio();
        String authorEmail = DataGenerator.generateEmail();
        
        Response authorResponse = authorServiceClient.createAuthor(authorName, authorBio, authorEmail);
        String authorId = new JSONObject(authorResponse.getBody().asString()).getString("id");
        
        String bookName = DataGenerator.generateBookName();
        String bookDescription = DataGenerator.generateBookDescription();
        BigDecimal bookPrice = DataGenerator.generateBookPrice();
        String skuCode = DataGenerator.generateSkuCode();
        
        bookServiceClient.createBook(bookName, bookDescription, bookPrice, authorId, skuCode);
        
        int initialStockQuantity = 10;
        stockCheckServiceClient.createStock(skuCode, initialStockQuantity);
        
        // Step 2: Create an order
        String orderNumber = DataGenerator.generateOrderNumber();
        String customerEmail = DataGenerator.generateEmail();
        int orderQuantity = 3;
        
        Map<String, Object> orderItem = new HashMap<>();
        orderItem.put("skuCode", skuCode);
        orderItem.put("price", bookPrice);
        orderItem.put("quantity", orderQuantity);
        
        List<Map<String, Object>> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        
        Response orderResponse = orderServiceClient.createOrder(orderNumber, customerEmail, orderItems);
        String orderId = new JSONObject(orderResponse.getBody().asString()).getString("id");
        
        // Step 3: Verify stock was reduced
        Response stockAfterOrderResponse = stockCheckServiceClient.checkStock(skuCode);
        JSONObject stockAfterOrderJson = new JSONObject(stockAfterOrderResponse.getBody().asString());
        AssertionUtils.assertJsonValue(stockAfterOrderJson, "quantity", initialStockQuantity - orderQuantity);
        
        // Step 4: Cancel the order
        Response cancelResponse = orderServiceClient.cancelOrder(orderId);
        AssertionUtils.assertStatusCode(cancelResponse, 200);
        JSONObject cancelJson = new JSONObject(cancelResponse.getBody().asString());
        AssertionUtils.assertJsonValue(cancelJson, "orderStatus", "CANCELLED");
        
        // Step 5: Verify stock has been restored
        Response stockAfterCancelResponse = stockCheckServiceClient.checkStock(skuCode);
        JSONObject stockAfterCancelJson = new JSONObject(stockAfterCancelResponse.getBody().asString());
        AssertionUtils.assertJsonValue(stockAfterCancelJson, "quantity", initialStockQuantity);
    }
    
    @Test(priority = 3)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test availability check when stock is depleted")
    @Story("Stock Depletion Flow")
    public void testStockDepletionFlow() {
        // Step 1: Create a book with limited stock
        String authorName = DataGenerator.generateAuthorName();
        String authorBio = DataGenerator.generateAuthorBio();
        String authorEmail = DataGenerator.generateEmail();
        
        Response authorResponse = authorServiceClient.createAuthor(authorName, authorBio, authorEmail);
        String authorId = new JSONObject(authorResponse.getBody().asString()).getString("id");
        
        String bookName = DataGenerator.generateBookName();
        String bookDescription = DataGenerator.generateBookDescription();
        BigDecimal bookPrice = DataGenerator.generateBookPrice();
        String skuCode = DataGenerator.generateSkuCode();
        
        bookServiceClient.createBook(bookName, bookDescription, bookPrice, authorId, skuCode);
        
        int limitedStock = 3;
        stockCheckServiceClient.createStock(skuCode, limitedStock);
        
        // Step 2: Create an order that depletes stock
        String orderNumber = DataGenerator.generateOrderNumber();
        String customerEmail = DataGenerator.generateEmail();
        
        Map<String, Object> orderItem = new HashMap<>();
        orderItem.put("skuCode", skuCode);
        orderItem.put("price", bookPrice);
        orderItem.put("quantity", limitedStock);
        
        List<Map<String, Object>> orderItems = new ArrayList<>();
        orderItems.add(orderItem);
        
        Response orderResponse = orderServiceClient.createOrder(orderNumber, customerEmail, orderItems);
        AssertionUtils.assertStatusCode(orderResponse, 201);
        
        // Step 3: Verify stock is now zero
        Response stockResponse = stockCheckServiceClient.checkStock(skuCode);
        JSONObject stockJson = new JSONObject(stockResponse.getBody().asString());
        AssertionUtils.assertJsonValue(stockJson, "quantity", 0);
        AssertionUtils.assertJsonValue(stockJson, "isInStock", false);
        
        // Step 4: Try to create another order for the same book
        String secondOrderNumber = DataGenerator.generateOrderNumber();
        String secondCustomerEmail = DataGenerator.generateEmail();
        
        Map<String, Object> secondOrderItem = new HashMap<>();
        secondOrderItem.put("skuCode", skuCode);
        secondOrderItem.put("price", bookPrice);
        secondOrderItem.put("quantity", 1);
        
        List<Map<String, Object>> secondOrderItems = new ArrayList<>();
        secondOrderItems.add(secondOrderItem);
        
        Response secondOrderResponse = orderServiceClient.createOrder(secondOrderNumber, secondCustomerEmail, secondOrderItems);
        
        // The service should either return a 4xx error or a specific error message
        // Adjust assertion based on actual service behavior
        AssertionUtils.assertTrue(secondOrderResponse.getStatusCode() >= 400 || 
            secondOrderResponse.getBody().asString().contains("insufficient stock"),
            "Order with out-of-stock items should fail");
    }
}