package config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Minimal test configuration for E2E API tests
 */
public class TestConfig {
    
    private static TestConfig instance;
    private final Properties properties;
    
    private TestConfig() {
        properties = new Properties();
        loadProperties();
    }
    
    public static TestConfig getInstance() {
        if (instance == null) {
            synchronized (TestConfig.class) {
                if (instance == null) {
                    instance = new TestConfig();
                }
            }
        }
        return instance;
    }
    
    private void loadProperties() {
        // Try to load from test.properties file
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("test.properties")) {
            if (input != null) {
                properties.load(input);
            }
        } catch (IOException e) {
            System.err.println("Warning: Could not load test.properties file: " + e.getMessage());
        }
        
        // Load system properties and environment variables
        properties.putAll(System.getProperties());
        System.getenv().forEach(properties::setProperty);
    }
    
    /**
     * Get API Gateway base URL for E2E tests
     */
    public String getApiGatewayBaseUrl() {
        return properties.getProperty("api.gateway.base.url", "http://localhost:8080");
    }
    
    /**
     * Get property value with default fallback
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue);
    }
}