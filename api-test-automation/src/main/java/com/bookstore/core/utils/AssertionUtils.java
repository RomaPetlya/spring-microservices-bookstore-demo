package com.bookstore.core.utils;

import io.restassured.response.Response;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.SoftAssertions;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Utility class for API response assertions.
 * Provides common assertion patterns for REST and GraphQL responses.
 */
public class AssertionUtils {
    
    /**
     * Verifies common success response attributes.
     *
     * @param response The REST response to verify
     * @param expectedStatusCode The expected HTTP status code
     */
    public static void assertSuccessResponse(Response response, int expectedStatusCode) {
        Assertions.assertThat(response.getStatusCode())
                .as("Status code should be %d", expectedStatusCode)
                .isEqualTo(expectedStatusCode);
        
        Assertions.assertThat(response.getContentType())
                .as("Content type should be JSON")
                .containsIgnoringCase("json");
    }
    
    /**
     * Performs multiple assertions on a response using SoftAssertions.
     *
     * @param response The REST response to verify
     * @param expectedStatusCode The expected HTTP status code
     * @param assertions Consumer that performs additional assertions using the provided SoftAssertions
     */
    public static void assertSoftly(Response response, int expectedStatusCode, Consumer<SoftAssertions> assertions) {
        SoftAssertions softly = new SoftAssertions();
        
        softly.assertThat(response.getStatusCode())
                .as("Status code should be %d", expectedStatusCode)
                .isEqualTo(expectedStatusCode);
        
        softly.assertThat(response.getContentType())
                .as("Content type should be JSON")
                .containsIgnoringCase("json");
        
        assertions.accept(softly);
        
        softly.assertAll();
    }
    
    /**
     * Verifies that a JSON response contains all the specified fields.
     *
     * @param response The REST response to verify
     * @param fields List of field paths to check
     */
    public static void assertResponseContainsFields(Response response, List<String> fields) {
        SoftAssertions softly = new SoftAssertions();
        
        for (String field : fields) {
            Object value = response.getBody().jsonPath().get(field);
            softly.assertThat(value)
                    .as("Response should contain field: %s", field)
                    .isNotNull();
        }
        
        softly.assertAll();
    }
    
    /**
     * Verifies that a JSON response contains all the specified field-value pairs.
     *
     * @param response The REST response to verify
     * @param fieldValuePairs Map of field paths to expected values
     */
    public static void assertResponseFieldValues(Response response, Map<String, Object> fieldValuePairs) {
        SoftAssertions softly = new SoftAssertions();
        
        fieldValuePairs.forEach((field, expectedValue) -> {
            Object value = response.getBody().jsonPath().get(field);
            softly.assertThat(value)
                    .as("Field %s should have value: %s", field, expectedValue)
                    .isEqualTo(expectedValue);
        });
        
        softly.assertAll();
    }
    
    /**
     * Verifies that a list in the response has the expected size.
     *
     * @param response The REST response to verify
     * @param listPath The JSON path to the list
     * @param expectedSize The expected size of the list
     */
    public static void assertListSize(Response response, String listPath, int expectedSize) {
        List<?> list = response.getBody().jsonPath().getList(listPath);
        Assertions.assertThat(list)
                .as("List at path '%s' should have size %d", listPath, expectedSize)
                .hasSize(expectedSize);
    }
    
    /**
     * Verifies that a response represents an error with the expected status code.
     *
     * @param response The REST response to verify
     * @param expectedStatusCode The expected HTTP error status code
     */
    public static void assertErrorResponse(Response response, int expectedStatusCode) {
        Assertions.assertThat(response.getStatusCode())
                .as("Status code should be %d", expectedStatusCode)
                .isEqualTo(expectedStatusCode);
        
        // Most APIs return error details in the response body
        Assertions.assertThat(response.getBody().asString())
                .as("Error response should not be empty")
                .isNotEmpty();
    }
    
    /**
     * Asserts the HTTP status code of a response.
     *
     * @param response The REST response to verify
     * @param expectedStatusCode The expected HTTP status code
     */
    public static void assertStatusCode(Response response, int expectedStatusCode) {
        Assertions.assertThat(response.getStatusCode())
                .as("Status code should be %d", expectedStatusCode)
                .isEqualTo(expectedStatusCode);
    }
    
    /**
     * Asserts that a JSON value matches an expected value.
     *
     * @param json The JSON object
     * @param path The path to the field
     * @param expectedValue The expected string value
     */
    public static void assertJsonValue(JSONObject json, String path, String expectedValue) {
        String actualValue = getJsonValueByPath(json, path).toString();
        Assertions.assertThat(actualValue)
                .as("JSON field %s should have value: %s", path, expectedValue)
                .isEqualTo(expectedValue);
    }
    
    /**
     * Asserts that a JSON value matches an expected integer value.
     *
     * @param json The JSON object
     * @param path The path to the field
     * @param expectedValue The expected integer value
     */
    public static void assertJsonValue(JSONObject json, String path, int expectedValue) {
        int actualValue = Integer.parseInt(getJsonValueByPath(json, path).toString());
        Assertions.assertThat(actualValue)
                .as("JSON field %s should have value: %d", path, expectedValue)
                .isEqualTo(expectedValue);
    }
    
    /**
     * Asserts that a JSON value matches an expected boolean value.
     *
     * @param json The JSON object
     * @param path The path to the field
     * @param expectedValue The expected boolean value
     */
    public static void assertJsonValue(JSONObject json, String path, boolean expectedValue) {
        boolean actualValue = Boolean.parseBoolean(getJsonValueByPath(json, path).toString());
        Assertions.assertThat(actualValue)
                .as("JSON field %s should have value: %b", path, expectedValue)
                .isEqualTo(expectedValue);
    }
    
    /**
     * Asserts that a value is not null.
     *
     * @param value The value to check
     * @param message The assertion message
     */
    public static void assertNotNull(String value, String message) {
        Assertions.assertThat(value)
                .as(message)
                .isNotNull();
    }
    
    /**
     * Asserts that a value in a JSON object is null.
     *
     * @param json The JSON object
     * @param path The path to the field
     */
    public static void assertNull(JSONObject json, String path) {
        Object value = getJsonValueByPath(json, path);
        Assertions.assertThat(value)
                .as("JSON field %s should be null", path)
                .isNull();
    }
    
    /**
     * Asserts that a JSON array field is not empty.
     *
     * @param json The JSON object
     * @param path The path to the array field
     */
    public static void assertJsonArrayNotEmpty(JSONObject json, String path) {
        JSONArray array = json.getJSONArray(path);
        Assertions.assertThat(array.length())
                .as("JSON array %s should not be empty", path)
                .isGreaterThan(0);
    }
    
    /**
     * Asserts that a JSON array field exists in the response.
     *
     * @param json The JSON object
     * @param path The path to the array field
     */
    public static void assertJsonArray(JSONObject json, String path) {
        JSONArray array = json.getJSONArray(path);
        Assertions.assertThat(array)
                .as("JSON field %s should be an array", path)
                .isNotNull();
    }
    
    /**
     * Asserts that a JSON response is an array.
     *
     * @param response The REST response to verify
     */
    public static void assertJsonArray(Response response) {
        String body = response.getBody().asString();
        Assertions.assertThat(body.trim().startsWith("["))
                .as("Response should be a JSON array")
                .isTrue();
    }
    
    /**
     * Asserts that a string contains another string.
     *
     * @param actual The actual string
     * @param expected The expected substring
     */
    public static void assertContains(String actual, String expected) {
        Assertions.assertThat(actual)
                .as("String should contain: %s", expected)
                .contains(expected);
    }
    
    /**
     * Asserts that a condition is true.
     *
     * @param condition The condition to check
     * @param message The assertion message
     */
    public static void assertTrue(boolean condition, String message) {
        Assertions.assertThat(condition)
                .as(message)
                .isTrue();
    }
    
    /**
     * Asserts that two integer values are equal.
     *
     * @param actual The actual value
     * @param expected The expected value
     */
    public static void assertEquals(int actual, int expected) {
        Assertions.assertThat(actual)
                .as("Expected %d but was %d", expected, actual)
                .isEqualTo(expected);
    }
    
    // Helper method to navigate JSON paths (simple implementation)
    private static Object getJsonValueByPath(JSONObject json, String path) {
        if (path.contains(".")) {
            String[] parts = path.split("\\.", 2);
            return getJsonValueByPath(json.getJSONObject(parts[0]), parts[1]);
        }
        return json.get(path);
    }
}