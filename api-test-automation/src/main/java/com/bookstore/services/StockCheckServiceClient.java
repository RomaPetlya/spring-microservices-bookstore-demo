package com.bookstore.services;

import com.bookstore.core.api.RestClient;
import com.bookstore.core.config.Environment;
import io.qameta.allure.Step;
import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;

/**
 * Client for interacting with the Stock Check Service API.
 */
@Slf4j
public class StockCheckServiceClient {
    private final RestClient restClient;
    private final String baseUrl;
    
    public StockCheckServiceClient() {
        Environment env = Environment.getInstance();
        this.baseUrl = env.getStockCheckServiceUrl();
        this.restClient = new RestClient(this.baseUrl);
        log.info("Initialized StockCheckServiceClient with base URL: {}", baseUrl);
    }
    
    /**
     * Checks stock availability for a SKU.
     *
     * @param skuCode The SKU code
     * @return The response containing stock information
     */
    @Step("Check stock for SKU: {0}")
    public Response checkStock(String skuCode) {
        log.info("Checking stock for SKU: {}", skuCode);
        return restClient.get("/api/v1/stock/" + skuCode);
    }
    
    /**
     * Updates stock quantity for a SKU.
     *
     * @param skuCode The SKU code
     * @param quantity The new quantity
     * @return The response containing the updated stock information
     */
    @Step("Update stock for SKU: {0} to quantity: {1}")
    public Response updateStock(String skuCode, int quantity) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("skuCode", skuCode);
        requestBody.put("quantity", quantity);
        
        log.info("Updating stock for SKU: {} to quantity: {}", skuCode, quantity);
        return restClient.put("/api/v1/stock", requestBody.toString());
    }
    
    /**
     * Creates a new stock entry.
     *
     * @param skuCode The SKU code
     * @param quantity The initial quantity
     * @return The response containing the created stock information
     */
    @Step("Create stock entry for SKU: {0} with quantity: {1}")
    public Response createStock(String skuCode, int quantity) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("skuCode", skuCode);
        requestBody.put("quantity", quantity);
        
        log.info("Creating stock entry for SKU: {} with quantity: {}", skuCode, quantity);
        return restClient.post("/api/v1/stock", requestBody.toString());
    }
    
    /**
     * Reserves stock for an order.
     *
     * @param skuCode The SKU code
     * @param quantity The quantity to reserve
     * @return The response from the reservation operation
     */
    @Step("Reserve stock for SKU: {0}, quantity: {1}")
    public Response reserveStock(String skuCode, int quantity) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("skuCode", skuCode);
        requestBody.put("quantity", quantity);
        
        log.info("Reserving stock for SKU: {}, quantity: {}", skuCode, quantity);
        return restClient.post("/api/v1/stock/reserve", requestBody.toString());
    }
    
    /**
     * Releases stock from a reservation.
     *
     * @param skuCode The SKU code
     * @param quantity The quantity to release
     * @return The response from the release operation
     */
    @Step("Release stock for SKU: {0}, quantity: {1}")
    public Response releaseStock(String skuCode, int quantity) {
        JSONObject requestBody = new JSONObject();
        requestBody.put("skuCode", skuCode);
        requestBody.put("quantity", quantity);
        
        log.info("Releasing stock for SKU: {}, quantity: {}", skuCode, quantity);
        return restClient.post("/api/v1/stock/release", requestBody.toString());
    }
}