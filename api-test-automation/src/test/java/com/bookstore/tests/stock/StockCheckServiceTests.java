package com.bookstore.tests.stock;

import com.bookstore.core.data.DataGenerator;
import com.bookstore.core.utils.AssertionUtils;
import com.bookstore.services.StockCheckServiceClient;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.json.JSONObject;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

@Epic("Stock Check Service API Tests")
@Feature("Stock Management")
public class StockCheckServiceTests {
    private StockCheckServiceClient stockCheckServiceClient;
    private String testSkuCode;
    private int initialQuantity;
    
    @BeforeClass
    public void setup() {
        stockCheckServiceClient = new StockCheckServiceClient();
        testSkuCode = DataGenerator.generateSkuCode();
        initialQuantity = DataGenerator.generateQuantity();
    }
    
    @Test(priority = 1)
    @Severity(SeverityLevel.BLOCKER)
    @Description("Create a new stock entry")
    @Story("Create Stock")
    public void testCreateStock() {
        Response response = stockCheckServiceClient.createStock(testSkuCode, initialQuantity);
        
        AssertionUtils.assertStatusCode(response, 201);
        JSONObject jsonResponse = new JSONObject(response.getBody().asString());
        AssertionUtils.assertJsonValue(jsonResponse, "skuCode", testSkuCode);
        AssertionUtils.assertJsonValue(jsonResponse, "quantity", initialQuantity);
        AssertionUtils.assertNotNull(jsonResponse.getString("id"), "Stock ID should not be null");
    }
    
    @Test(priority = 2, dependsOnMethods = "testCreateStock")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Check stock availability")
    @Story("Check Stock")
    public void testCheckStock() {
        Response response = stockCheckServiceClient.checkStock(testSkuCode);
        
        AssertionUtils.assertStatusCode(response, 200);
        JSONObject jsonResponse = new JSONObject(response.getBody().asString());
        AssertionUtils.assertJsonValue(jsonResponse, "skuCode", testSkuCode);
        AssertionUtils.assertJsonValue(jsonResponse, "quantity", initialQuantity);
        AssertionUtils.assertJsonValue(jsonResponse, "isInStock", true);
    }
    
    @Test(priority = 3, dependsOnMethods = "testCreateStock")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Update stock quantity")
    @Story("Update Stock")
    public void testUpdateStock() {
        int updatedQuantity = initialQuantity + 5;
        
        Response response = stockCheckServiceClient.updateStock(testSkuCode, updatedQuantity);
        
        AssertionUtils.assertStatusCode(response, 200);
        JSONObject jsonResponse = new JSONObject(response.getBody().asString());
        AssertionUtils.assertJsonValue(jsonResponse, "skuCode", testSkuCode);
        AssertionUtils.assertJsonValue(jsonResponse, "quantity", updatedQuantity);
    }
    
    @Test(priority = 4, dependsOnMethods = "testUpdateStock")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Reserve stock")
    @Story("Reserve Stock")
    public void testReserveStock() {
        int quantityToReserve = 2;
        
        Response response = stockCheckServiceClient.reserveStock(testSkuCode, quantityToReserve);
        
        AssertionUtils.assertStatusCode(response, 200);
        JSONObject jsonResponse = new JSONObject(response.getBody().asString());
        AssertionUtils.assertJsonValue(jsonResponse, "reserved", true);
        
        // Verify the quantity has been reduced
        Response checkResponse = stockCheckServiceClient.checkStock(testSkuCode);
        JSONObject checkJson = new JSONObject(checkResponse.getBody().asString());
        int newQuantity = checkJson.getInt("quantity");
        AssertionUtils.assertEquals(newQuantity, initialQuantity + 5 - quantityToReserve);
    }
    
    @Test(priority = 5, dependsOnMethods = "testReserveStock")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Release stock")
    @Story("Release Stock")
    public void testReleaseStock() {
        int quantityToRelease = 1;
        
        Response response = stockCheckServiceClient.releaseStock(testSkuCode, quantityToRelease);
        
        AssertionUtils.assertStatusCode(response, 200);
        JSONObject jsonResponse = new JSONObject(response.getBody().asString());
        AssertionUtils.assertJsonValue(jsonResponse, "released", true);
        
        // Verify the quantity has been increased
        Response checkResponse = stockCheckServiceClient.checkStock(testSkuCode);
        JSONObject checkJson = new JSONObject(checkResponse.getBody().asString());
        int newQuantity = checkJson.getInt("quantity");
        AssertionUtils.assertEquals(newQuantity, initialQuantity + 5 - 2 + quantityToRelease);
    }
    
    @Test(priority = 6)
    @Severity(SeverityLevel.NORMAL)
    @Description("Check stock for non-existent SKU")
    @Story("Check Stock")
    public void testCheckNonExistentStock() {
        String nonExistentSku = "NONEXIST-" + System.currentTimeMillis();
        Response response = stockCheckServiceClient.checkStock(nonExistentSku);
        
        AssertionUtils.assertStatusCode(response, 404);
    }
    
    @Test(priority = 7)
    @Severity(SeverityLevel.CRITICAL)
    @Description("Test out of stock scenario")
    @Story("Check Stock")
    public void testOutOfStockScenario() {
        // Create a new stock with 0 quantity
        String zeroStockSku = DataGenerator.generateSkuCode();
        stockCheckServiceClient.createStock(zeroStockSku, 0);
        
        // Verify it's marked as out of stock
        Response response = stockCheckServiceClient.checkStock(zeroStockSku);
        
        AssertionUtils.assertStatusCode(response, 200);
        JSONObject jsonResponse = new JSONObject(response.getBody().asString());
        AssertionUtils.assertJsonValue(jsonResponse, "isInStock", false);
    }
}