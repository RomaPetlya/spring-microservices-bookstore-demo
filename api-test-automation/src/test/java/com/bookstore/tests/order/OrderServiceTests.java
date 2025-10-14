package com.bookstore.tests.order;

import com.bookstore.core.data.DataGenerator;
import com.bookstore.core.utils.AssertionUtils;
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

@Epic("Order Service API Tests")
@Feature("Order Management")
public class OrderServiceTests {
    private OrderServiceClient orderServiceClient;
    private StockCheckServiceClient stockCheckServiceClient;
    private String testOrderId;
    private String testOrderNumber;
    private String testCustomerEmail;
    private List<Map<String, Object>> testOrderItems;
    
    @BeforeClass
    public void setup() {
        orderServiceClient = new OrderServiceClient();
        stockCheckServiceClient = new StockCheckServiceClient();
        
        // Prepare test data
        testOrderNumber = DataGenerator.generateOrderNumber();
        testCustomerEmail = DataGenerator.generateEmail();
        
        // Create test order items with valid stock
        testOrderItems = new ArrayList<>();
        
        // First item
        String skuCode1 = DataGenerator.generateSkuCode();
        BigDecimal price1 = DataGenerator.generateBookPrice();
        int quantity1 = 2;
        
        Map<String, Object> item1 = new HashMap<>();
        item1.put("skuCode", skuCode1);
        item1.put("price", price1);
        item1.put("quantity", quantity1);
        testOrderItems.add(item1);
        
        // Create stock for first item
        stockCheckServiceClient.createStock(skuCode1, 10);
        
        // Second item
        String skuCode2 = DataGenerator.generateSkuCode();
        BigDecimal price2 = DataGenerator.generateBookPrice();
        int quantity2 = 1;
        
        Map<String, Object> item2 = new HashMap<>();
        item2.put("skuCode", skuCode2);
        item2.put("price", price2);
        item2.put("quantity", quantity2);
        testOrderItems.add(item2);
        
        // Create stock for second item
        stockCheckServiceClient.createStock(skuCode2, 5);
    }
    
    @Test(priority = 1)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Create a new order")
    @Story("Create Order")
    public void testCreateOrder() {
        Response response = orderServiceClient.createOrder(testOrderNumber, testCustomerEmail, testOrderItems);
        
        AssertionUtils.assertStatusCode(response, 201);
        JSONObject jsonResponse = new JSONObject(response.getBody().asString());
        AssertionUtils.assertJsonValue(jsonResponse, "orderNumber", testOrderNumber);
        AssertionUtils.assertJsonValue(jsonResponse, "customerEmail", testCustomerEmail);
        AssertionUtils.assertNotNull(jsonResponse.getString("id"), "Order ID should not be null");
        AssertionUtils.assertJsonArray(jsonResponse, "orderItems");
        AssertionUtils.assertJsonValue(jsonResponse, "orderStatus", "PLACED");
        
        // Store order ID for future tests
        testOrderId = jsonResponse.getString("id");
    }
    
    @Test(priority = 2, dependsOnMethods = "testCreateOrder")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Get order by ID")
    @Story("Retrieve Order")
    public void testGetOrderById() {
        Response response = orderServiceClient.getOrderById(testOrderId);
        
        AssertionUtils.assertStatusCode(response, 200);
        JSONObject jsonResponse = new JSONObject(response.getBody().asString());
        AssertionUtils.assertJsonValue(jsonResponse, "id", testOrderId);
        AssertionUtils.assertJsonValue(jsonResponse, "orderNumber", testOrderNumber);
    }
    
    @Test(priority = 3)
    @Severity(SeverityLevel.NORMAL)
    @Description("Get all orders")
    @Story("Retrieve Orders")
    public void testGetAllOrders() {
        Response response = orderServiceClient.getAllOrders();
        
        AssertionUtils.assertStatusCode(response, 200);
        AssertionUtils.assertJsonArray(response);
    }
    
    @Test(priority = 4, dependsOnMethods = "testCreateOrder")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Get orders by customer email")
    @Story("Retrieve Orders by Customer")
    public void testGetOrdersByCustomerEmail() {
        Response response = orderServiceClient.getOrdersByCustomerEmail(testCustomerEmail);
        
        AssertionUtils.assertStatusCode(response, 200);
        AssertionUtils.assertJsonArray(response);
        
        String responseBody = response.getBody().asString();
        AssertionUtils.assertContains(responseBody, testOrderNumber);
    }
    
    @Test(priority = 5, dependsOnMethods = "testCreateOrder")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Update order status")
    @Story("Update Order")
    public void testUpdateOrderStatus() {
        String newStatus = "PROCESSING";
        
        Response response = orderServiceClient.updateOrderStatus(testOrderId, newStatus);
        
        AssertionUtils.assertStatusCode(response, 200);
        JSONObject jsonResponse = new JSONObject(response.getBody().asString());
        AssertionUtils.assertJsonValue(jsonResponse, "id", testOrderId);
        AssertionUtils.assertJsonValue(jsonResponse, "orderStatus", newStatus);
    }
    
    @Test(priority = 6)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Cancel an order")
    @Story("Cancel Order")
    public void testCancelOrder() {
        // Create a new order to cancel
        String orderNumber = DataGenerator.generateOrderNumber();
        String customerEmail = DataGenerator.generateEmail();
        
        Response createResponse = orderServiceClient.createOrder(orderNumber, customerEmail, testOrderItems);
        JSONObject createJson = new JSONObject(createResponse.getBody().asString());
        String orderIdToCancel = createJson.getString("id");
        
        // Cancel the order
        Response cancelResponse = orderServiceClient.cancelOrder(orderIdToCancel);
        AssertionUtils.assertStatusCode(cancelResponse, 200);
        JSONObject cancelJson = new JSONObject(cancelResponse.getBody().asString());
        AssertionUtils.assertJsonValue(cancelJson, "orderStatus", "CANCELLED");
        
        // Verify the order status has been updated
        Response getResponse = orderServiceClient.getOrderById(orderIdToCancel);
        JSONObject getJson = new JSONObject(getResponse.getBody().asString());
        AssertionUtils.assertJsonValue(getJson, "orderStatus", "CANCELLED");
    }
    
    @Test(priority = 7)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test order with insufficient stock")
    @Story("Create Order")
    public void testOrderWithInsufficientStock() {
        String orderNumber = DataGenerator.generateOrderNumber();
        String customerEmail = DataGenerator.generateEmail();
        
        // Create an order item with insufficient stock
        String skuCode = DataGenerator.generateSkuCode();
        BigDecimal price = DataGenerator.generateBookPrice();
        int quantity = 10;
        
        Map<String, Object> item = new HashMap<>();
        item.put("skuCode", skuCode);
        item.put("price", price);
        item.put("quantity", quantity);
        
        List<Map<String, Object>> orderItems = new ArrayList<>();
        orderItems.add(item);
        
        // Create stock with less quantity than the order requires
        stockCheckServiceClient.createStock(skuCode, 5);
        
        // Try to create the order
        Response response = orderServiceClient.createOrder(orderNumber, customerEmail, orderItems);
        
        // The service should either return a 4xx error or a specific error message
        // Adjust assertion based on actual service behavior
        AssertionUtils.assertTrue(response.getStatusCode() >= 400 || 
            response.getBody().asString().contains("insufficient stock"),
            "Order with insufficient stock should fail");
    }
}