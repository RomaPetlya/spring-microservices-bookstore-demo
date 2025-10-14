package com.bookstore.core.data;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Utility for loading test data from JSON files.
 */
@Slf4j
public class TestDataLoader {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Loads an object from a JSON file.
     *
     * @param <T> The type of object to load
     * @param filePath The path to the JSON file (from resources)
     * @param clazz The class of the object to load
     * @return The loaded object
     */
    public static <T> T loadObject(String filePath, Class<T> clazz) {
        try (InputStream is = TestDataLoader.class.getResourceAsStream(filePath)) {
            if (is == null) {
                log.error("Test data file not found: {}", filePath);
                throw new RuntimeException("Test data file not found: " + filePath);
            }
            
            T result = objectMapper.readValue(is, clazz);
            log.info("Loaded test data object of type {} from {}", clazz.getSimpleName(), filePath);
            return result;
        } catch (IOException e) {
            log.error("Error loading test data from {}: {}", filePath, e.getMessage());
            throw new RuntimeException("Failed to load test data from " + filePath, e);
        }
    }
    
    /**
     * Loads a list of objects from a JSON file.
     *
     * @param <T> The type of objects in the list
     * @param filePath The path to the JSON file (from resources)
     * @param elementClass The class of the objects in the list
     * @return The loaded list
     */
    public static <T> List<T> loadList(String filePath, Class<T> elementClass) {
        try (InputStream is = TestDataLoader.class.getResourceAsStream(filePath)) {
            if (is == null) {
                log.error("Test data file not found: {}", filePath);
                throw new RuntimeException("Test data file not found: " + filePath);
            }
            
            List<T> result = objectMapper.readValue(is, 
                    objectMapper.getTypeFactory().constructCollectionType(List.class, elementClass));
            log.info("Loaded list of {} objects from {}", elementClass.getSimpleName(), filePath);
            return result;
        } catch (IOException e) {
            log.error("Error loading test data list from {}: {}", filePath, e.getMessage());
            throw new RuntimeException("Failed to load test data list from " + filePath, e);
        }
    }
    
    /**
     * Loads a map from a JSON file.
     *
     * @param <K> The key type
     * @param <V> The value type
     * @param filePath The path to the JSON file (from resources)
     * @param keyClass The class of the keys
     * @param valueClass The class of the values
     * @return The loaded map
     */
    public static <K, V> Map<K, V> loadMap(String filePath, Class<K> keyClass, Class<V> valueClass) {
        try (InputStream is = TestDataLoader.class.getResourceAsStream(filePath)) {
            if (is == null) {
                log.error("Test data file not found: {}", filePath);
                throw new RuntimeException("Test data file not found: " + filePath);
            }
            
            Map<K, V> result = objectMapper.readValue(is, 
                    objectMapper.getTypeFactory().constructMapType(Map.class, keyClass, valueClass));
            log.info("Loaded map with keys of type {} and values of type {} from {}", 
                    keyClass.getSimpleName(), valueClass.getSimpleName(), filePath);
            return result;
        } catch (IOException e) {
            log.error("Error loading test data map from {}: {}", filePath, e.getMessage());
            throw new RuntimeException("Failed to load test data map from " + filePath, e);
        }
    }
    
    /**
     * Loads a generic object from a JSON file using a TypeReference.
     * This is useful for complex generic types.
     *
     * @param <T> The type of object to load
     * @param filePath The path to the JSON file (from resources)
     * @param typeReference The TypeReference describing the object type
     * @return The loaded object
     */
    public static <T> T load(String filePath, TypeReference<T> typeReference) {
        try (InputStream is = TestDataLoader.class.getResourceAsStream(filePath)) {
            if (is == null) {
                log.error("Test data file not found: {}", filePath);
                throw new RuntimeException("Test data file not found: " + filePath);
            }
            
            T result = objectMapper.readValue(is, typeReference);
            log.info("Loaded test data from {}", filePath);
            return result;
        } catch (IOException e) {
            log.error("Error loading test data from {}: {}", filePath, e.getMessage());
            throw new RuntimeException("Failed to load test data from " + filePath, e);
        }
    }
}