package com.bookstore.core.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Loads and provides access to test configuration properties from YAML files.
 */
@Slf4j
public class TestProperties {
    private static final String DEFAULT_CONFIG_FILE = "/config/application.yml";
    private static final String ENV_CONFIG_FILE_FORMAT = "/config/application-%s.yml";
    
    private static TestProperties instance;
    private final Map<String, Object> properties = new HashMap<>();
    
    private TestProperties() {
        loadDefaultProperties();
        loadEnvironmentProperties();
    }
    
    /**
     * Gets the singleton instance of TestProperties.
     *
     * @return The TestProperties instance
     */
    public static synchronized TestProperties getInstance() {
        if (instance == null) {
            instance = new TestProperties();
        }
        return instance;
    }
    
    /**
     * Gets a property value as a String.
     *
     * @param key The property key
     * @return The property value, or null if not found
     */
    public String getProperty(String key) {
        Object value = getPropertyValue(key);
        return value != null ? value.toString() : null;
    }
    
    /**
     * Gets a property value as a String with a default value.
     *
     * @param key The property key
     * @param defaultValue The default value to return if the property is not found
     * @return The property value, or the default value if not found
     */
    public String getProperty(String key, String defaultValue) {
        return Optional.ofNullable(getProperty(key)).orElse(defaultValue);
    }
    
    /**
     * Gets a property value as an Integer.
     *
     * @param key The property key
     * @return The property value as an Integer, or null if not found or not an integer
     */
    public Integer getIntProperty(String key) {
        String value = getProperty(key);
        if (value == null) {
            return null;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            log.warn("Property {} is not an integer: {}", key, value);
            return null;
        }
    }
    
    /**
     * Gets a property value as an Integer with a default value.
     *
     * @param key The property key
     * @param defaultValue The default value to return if the property is not found or not an integer
     * @return The property value as an Integer, or the default value if not found or not an integer
     */
    public Integer getIntProperty(String key, Integer defaultValue) {
        return Optional.ofNullable(getIntProperty(key)).orElse(defaultValue);
    }
    
    /**
     * Gets a property value as a Boolean.
     *
     * @param key The property key
     * @return The property value as a Boolean, or null if not found
     */
    public Boolean getBooleanProperty(String key) {
        String value = getProperty(key);
        return value != null ? Boolean.parseBoolean(value) : null;
    }
    
    /**
     * Gets a property value as a Boolean with a default value.
     *
     * @param key The property key
     * @param defaultValue The default value to return if the property is not found
     * @return The property value as a Boolean, or the default value if not found
     */
    public Boolean getBooleanProperty(String key, Boolean defaultValue) {
        return Optional.ofNullable(getBooleanProperty(key)).orElse(defaultValue);
    }
    
    /**
     * Gets a nested property value.
     *
     * @param keys The property key path as an array of keys
     * @return The property value, or null if not found
     */
    @SuppressWarnings("unchecked")
    public Object getNestedProperty(String... keys) {
        if (keys.length == 0) {
            return null;
        }
        
        Map<String, Object> currentMap = properties;
        for (int i = 0; i < keys.length - 1; i++) {
            Object value = currentMap.get(keys[i]);
            if (!(value instanceof Map)) {
                return null;
            }
            currentMap = (Map<String, Object>) value;
        }
        
        return currentMap.get(keys[keys.length - 1]);
    }
    
    private void loadDefaultProperties() {
        try (InputStream inputStream = getClass().getResourceAsStream(DEFAULT_CONFIG_FILE)) {
            if (inputStream != null) {
                ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                Map<String, Object> loadedProps = mapper.readValue(inputStream, Map.class);
                properties.putAll(loadedProps);
                log.info("Loaded default properties from {}", DEFAULT_CONFIG_FILE);
            } else {
                log.warn("Default configuration file not found: {}", DEFAULT_CONFIG_FILE);
            }
        } catch (IOException e) {
            log.error("Error loading default properties", e);
        }
    }
    
    private void loadEnvironmentProperties() {
        String env = System.getProperty("env");
        if (env == null || env.isEmpty()) {
            log.info("No specific environment set. Using default properties only.");
            return;
        }
        
        String envConfigFile = String.format(ENV_CONFIG_FILE_FORMAT, env);
        try (InputStream inputStream = getClass().getResourceAsStream(envConfigFile)) {
            if (inputStream != null) {
                ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
                Map<String, Object> envProps = mapper.readValue(inputStream, Map.class);
                properties.putAll(envProps);
                log.info("Loaded environment properties from {}", envConfigFile);
            } else {
                log.warn("Environment configuration file not found: {}", envConfigFile);
            }
        } catch (IOException e) {
            log.error("Error loading environment properties", e);
        }
    }
    
    private Object getPropertyValue(String key) {
        String[] nestedKeys = key.split("\\.");
        if (nestedKeys.length > 1) {
            return getNestedProperty(nestedKeys);
        } else {
            return properties.get(key);
        }
    }
}