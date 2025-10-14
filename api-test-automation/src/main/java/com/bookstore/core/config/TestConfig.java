package com.bookstore.core.config;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.config.HttpClientConfig;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import lombok.extern.slf4j.Slf4j;

/**
 * General test configuration class.
 * Initializes global settings for the test framework.
 */
@Slf4j
public class TestConfig {
    private static final Environment ENV = Environment.getInstance();
    private static boolean initialized = false;
    
    /**
     * Initializes the test configuration.
     * Sets up global REST-assured configuration, logging, etc.
     */
    public static synchronized void init() {
        if (initialized) {
            return;
        }
        
        configureRestAssured();
        
        log.info("Test framework initialized with environment settings");
        initialized = true;
    }
    
    /**
     * Configures global REST-assured settings.
     */
    private static void configureRestAssured() {
        RestAssured.config = RestAssuredConfig.config()
                .logConfig(LogConfig.logConfig()
                        .enableLoggingOfRequestAndResponseIfValidationFails());
        
        // Add global filters for logging and Allure reporting
        RestAssured.filters(
                new RequestLoggingFilter(),
                new ResponseLoggingFilter(),
                new AllureRestAssured()
        );
        
        // Set default timeout
        RestAssured.config = RestAssured.config()
                .httpClient(HttpClientConfig.httpClientConfig()
                        .setParam("http.socket.timeout", ENV.getDefaultTimeoutSeconds() * 1000)
                        .setParam("http.connection.timeout", ENV.getDefaultTimeoutSeconds() * 1000));
        
        log.info("REST-assured configured with default timeout: {} seconds", 
                ENV.getDefaultTimeoutSeconds());
    }
}