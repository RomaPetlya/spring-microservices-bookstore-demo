package com.bookstore.services;

import com.bookstore.core.api.RestClient;
import com.bookstore.core.config.Environment;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Client for interacting with the Order Service API.
 */
@Slf4j
public class OrderServiceClient {
    private final RestClient restClient;
    private final String baseUrl;
    
    public OrderServiceClient() {
        Environment env = Environment.getInstance();
        this.baseUrl = env.getOrderServiceUrl();
        this.restClient = new RestClient(this.baseUrl);
        log.info("Initialized OrderServiceClient with base URL: {}", baseUrl);
    }
    
    /**
     * Creates a new order.
     *
     * @param orderNumber The order number
     * @param customerEmail The customer email
     * @param orderItems List of maps containing orderItems (skuCode, price, quantity)
     * @return The response containing the created order
     */
    @Step("Create order with orderNumber: {0}, customerEmail: {1}")
    public Response createOrder(String orderNumber, String customerEmail, List<Map<String, Object>> orderItems) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("orderNumber", orderNumber);
        requestBody.put("customerEmail", customerEmail);
        
        JSONArray itemsArray = new JSONArray();
        for (Map<String, Object> item : orderItems) {
            JSONObject orderItem = new JSONObject();
            orderItem.put("skuCode", item.get("skuCode"));
            orderItem.put("price", item.get("price"));
            orderItem.put("quantity", item.get("quantity"));
            itemsArray.put(orderItem);
        }
        requestBody.put("orderItems", itemsArray);
        
        log.info("Creating order with orderNumber: {}", orderNumber);
        return restClient.post("/api/v1/orders", requestBody.toString());
    }
    
    /**
     * Gets an order by ID.
     *
     * @param orderId The order ID
     * @return The response containing the order
     */
    @Step("Get order by ID: {0}")
    public Response getOrderById(String orderId) {
        log.info("Getting order with ID: {}", orderId);
        return restClient.get("/api/v1/orders/" + orderId);
    }
    
    /**
     * Gets all orders.
     *
     * @return The response containing all orders
     */
    @Step("Get all orders")
    public Response getAllOrders() {
        log.info("Getting all orders");
        return restClient.get("/api/v1/orders");
    }
    
    /**
     * Gets orders by customer email.
     *
     * @param email The customer email
     * @return The response containing the customer's orders
     */
    @Step("Get orders by customer email: {0}")
    public Response getOrdersByCustomerEmail(String email) {
        log.info("Getting orders for customer with email: {}", email);
        return restClient.get("/api/v1/orders/customer/" + email);
    }
    
    /**
     * Updates an order status.
     *
     * @param orderId The order ID
     * @param status The new status
     * @return The response containing the updated order
     */
    @Step("Update order status for ID: {0} to {1}")
    public Response updateOrderStatus(String orderId, String status) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("status", status);
        
        log.info("Updating order status to {} for order ID: {}", status, orderId);
        return restClient.put("/api/v1/orders/" + orderId + "/status", requestBody.toString());
    }
    
    /**
     * Cancels an order.
     *
     * @param orderId The order ID
     * @return The response from the cancel operation
     */
    @Step("Cancel order with ID: {0}")
    public Response cancelOrder(String orderId) {
        log.info("Canceling order with ID: {}", orderId);
        return restClient.post("/api/v1/orders/" + orderId + "/cancel", "");
    }
}