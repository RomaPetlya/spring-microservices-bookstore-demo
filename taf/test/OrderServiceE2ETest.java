package test;

import client.RestClient;
import config.TestConfig;
import com.fasterxml.jackson.core.type.TypeReference;
import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * E2E Tests for Order Service REST API
 * Tests: O-E2E-001 to O-E2E-005 from test cases document
 */
@Epic("E2E API Tests")
@Feature("Order Service REST")
@Tag("e2e")
@Tag("rest")
class OrderServiceE2ETest {

    private static RestClient restClient;
    private static String baseUrl;
    private static final String ORDER_ENDPOINT = "/api/order";

    @BeforeAll
    static void setUp() {
        restClient = new RestClient();
        baseUrl = TestConfig.getInstance().getApiGatewayBaseUrl();
    }

    @Test
    @Story("Order Placement")
    @DisplayName("O-E2E-001: Place an order for in-stock item")
    @Description("Place an order for an item that is currently in stock")
    @Severity(SeverityLevel.BLOCKER)
    void testPlaceOrderForInStockItem() {
        // Given
        String endpoint = baseUrl + ORDER_ENDPOINT;
        
        Map<String, Object> orderLineItem = new HashMap<>();
        orderLineItem.put("skuCode", "design_patterns_gof");
        orderLineItem.put("price", 29);
        orderLineItem.put("quantity", 1);
        
        List<Map<String, Object>> orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem);
        
        Map<String, Object> orderRequest = new HashMap<>();
        orderRequest.put("orderLineItemsDtoList", orderLineItems);
        
        // When
        Map<String, Object> response = restClient.post(endpoint, orderRequest, new TypeReference<Map<String, Object>>() {});
        
        // Then
        assertNotNull(response, "Order response should not be null");
        // STRICT validation: response MUST contain status field
        assertTrue(response.containsKey("status"), "Order response MUST contain status field");
        assertEquals("success", response.get("status"), "Order should be successful for in-stock item");
        
        // Verify message field exists and contains meaningful content
        assertTrue(response.containsKey("message"), "Order response MUST contain message field");
        String message = response.get("message").toString();
        assertTrue(message.contains("success") || message.contains("placed"), 
            "Success message should indicate order was placed");
        
        System.out.println("âœ… O-E2E-001: Successfully placed order for in-stock item - " + message);
    }

    @Test
    @Story("Order Placement")
    @DisplayName("O-E2E-002: Attempt to order out-of-stock item")
    @Description("Attempt to place an order for an item that is out of stock")
    @Severity(SeverityLevel.CRITICAL)
    void testPlaceOrderForOutOfStockItem() {
        // Given
        String endpoint = baseUrl + ORDER_ENDPOINT;
        
        Map<String, Object> orderLineItem = new HashMap<>();
        orderLineItem.put("skuCode", "mythical_man_month");
        orderLineItem.put("price", 39);
        orderLineItem.put("quantity", 1);
        
        List<Map<String, Object>> orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem);
        
        Map<String, Object> orderRequest = new HashMap<>();
        orderRequest.put("orderLineItemsDtoList", orderLineItems);
        
        // When & Then
        try {
            Map<String, Object> response = restClient.post(endpoint, orderRequest, new TypeReference<Map<String, Object>>() {});
            
            // If response is returned (not exception), it MUST have error status
            assertNotNull(response, "Response should not be null");
            assertTrue(response.containsKey("status"), "Response MUST contain status field");
            assertEquals("error", response.get("status"), "Order should fail for out-of-stock item");
            
            assertTrue(response.containsKey("message"), "Response MUST contain message field");
            String message = response.get("message").toString().toLowerCase();
            assertTrue(message.contains("out of stock") || message.contains("unavailable") || message.contains("not in stock"), 
                "Error message should indicate item is out of stock");
            
            System.out.println("âœ… O-E2E-002: Correctly handled out-of-stock order - " + response.get("message"));
            
        } catch (Exception e) {
            // 500 Internal Server Error is expected for business rule violations
            assertTrue(e.getMessage().contains("500") || e.getMessage().contains("Internal Server Error"),
                "Should receive 500 error for out-of-stock item order");
            System.out.println("âœ… O-E2E-002: Correctly returned 500 error for out-of-stock item");
        }
    }

    @Test
    @Story("Order Placement")
    @DisplayName("O-E2E-003: Place order with multiple items (mixed stock)")
    @Description("Place order with multiple items where some are in stock and some are not")
    @Severity(SeverityLevel.NORMAL)
    void testPlaceOrderWithMixedStock() {
        // Given
        String endpoint = baseUrl + ORDER_ENDPOINT;
        
        // In-stock item
        Map<String, Object> inStockItem = new HashMap<>();
        inStockItem.put("skuCode", "design_patterns_gof");
        inStockItem.put("price", 29);
        inStockItem.put("quantity", 1);
        
        // Out-of-stock item
        Map<String, Object> outOfStockItem = new HashMap<>();
        outOfStockItem.put("skuCode", "mythical_man_month");
        outOfStockItem.put("price", 39);
        outOfStockItem.put("quantity", 1);
        
        List<Map<String, Object>> orderLineItems = new ArrayList<>();
        orderLineItems.add(inStockItem);
        orderLineItems.add(outOfStockItem);
        
        Map<String, Object> orderRequest = new HashMap<>();
        orderRequest.put("orderLineItemsDtoList", orderLineItems);
        
        // When & Then
        try {
            Map<String, Object> response = restClient.post(endpoint, orderRequest, new TypeReference<Map<String, Object>>() {});
            
            // Should return error status for mixed stock
            assertNotNull(response, "Response should not be null");
            assertTrue(response.containsKey("status"), "Response MUST contain status field");
            assertEquals("error", response.get("status"), "Order should fail when some items are out of stock");
            
            assertTrue(response.containsKey("message"), "Response MUST contain message field");
            String message = response.get("message").toString().toLowerCase();
            assertTrue(message.contains("out of stock") || message.contains("unavailable") || message.contains("not in stock"), 
                "Error message should indicate some items are out of stock");
            
            System.out.println("âœ… O-E2E-003: Correctly handled mixed stock order - " + response.get("message"));
            
        } catch (Exception e) {
            // 500 error is expected for business rule violations
            assertTrue(e.getMessage().contains("500"),
                "Should receive 500 error for mixed stock order");
            System.out.println("âœ… O-E2E-003: Correctly returned 500 error for mixed stock order");
        }
    }

    @Test
    @Story("Order Validation")
    @DisplayName("O-E2E-004: Place order with invalid data (missing SKU)")
    @Description("Attempt to place an order with missing SKU code")
    @Severity(SeverityLevel.NORMAL)
    void testPlaceOrderWithMissingSku() {
        // Given
        String endpoint = baseUrl + ORDER_ENDPOINT;
        
        Map<String, Object> orderLineItem = new HashMap<>();
        // Missing skuCode field
        orderLineItem.put("price", 29);
        orderLineItem.put("quantity", 1);
        
        List<Map<String, Object>> orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem);
        
        Map<String, Object> orderRequest = new HashMap<>();
        orderRequest.put("orderLineItemsDtoList", orderLineItems);
        
        // When & Then
        try {
            restClient.post(endpoint, orderRequest, new TypeReference<Map<String, Object>>() {});
            fail("Should have thrown validation error for missing SKU");
            
        } catch (Exception e) {
            // Should receive 400 Bad Request for validation error
            assertTrue(e.getMessage().contains("400") || e.getMessage().contains("Bad Request"),
                "Should receive 400 Bad Request for missing SKU");
            System.out.println("âœ… O-E2E-004: Correctly rejected order with missing SKU");
        }
    }

    @Test
    @Story("Order Validation")
    @DisplayName("O-E2E-005: Place order with zero quantity - STRICT")
    @Description("Attempt to place an order with zero quantity - system MUST reject this")
    @Severity(SeverityLevel.CRITICAL)
    void testPlaceOrderWithZeroQuantity() {
        // Given
        String endpoint = baseUrl + ORDER_ENDPOINT;
        
        Map<String, Object> orderLineItem = new HashMap<>();
        orderLineItem.put("skuCode", "design_patterns_gof");
        orderLineItem.put("price", 29);
        orderLineItem.put("quantity", 0); // Zero quantity
        
        List<Map<String, Object>> orderLineItems = new ArrayList<>();
        orderLineItems.add(orderLineItem);
        
        Map<String, Object> orderRequest = new HashMap<>();
        orderRequest.put("orderLineItemsDtoList", orderLineItems);
        
        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            restClient.post(endpoint, orderRequest, new TypeReference<Map<String, Object>>() {});
        }, "System MUST reject orders with zero quantity");
        
        // Verify it's a validation error
        assertTrue(exception.getMessage().contains("400") || exception.getMessage().contains("Bad Request"),
            "Should receive HTTP 400 Bad Request for zero quantity");
        System.out.println("âœ… O-E2E-005: STRICT validation correctly rejected order with zero quantity");
    }
}