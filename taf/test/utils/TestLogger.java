package utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import java.util.UUID;

/**
 * Simplified structured logger for E2E tests with MDC support
 */
public class TestLogger {
    
    private final Logger logger;
    
    public TestLogger(Class<?> clazz) {
        this.logger = LoggerFactory.getLogger(clazz);
    }
    
    public TestLogger(String name) {
        this.logger = LoggerFactory.getLogger(name);
    }
    
    /**
     * Setup test context in MDC for parallel test tracking
     */
    public void setupTestContext(String testName) {
        MDC.put("testName", testName);
        MDC.put("testId", UUID.randomUUID().toString().substring(0, 8));
    }
    
    /**
     * Clear test context from MDC
     */
    public void clearTestContext() {
        MDC.clear();
    }
    
    /**
     * Log test start
     */
    public void testStart(String testId, String description) {
        logger.info("Starting test: {} - {}", testId, description);
    }
    
    /**
     * Log test completion with result
     */
    public void testComplete(String testId, boolean passed, long executionTimeMs) {
        if (passed) {
            logger.info("✅ Test {} PASSED - Execution time: {}ms", testId, executionTimeMs);
        } else {
            logger.error("❌ Test {} FAILED - Execution time: {}ms", testId, executionTimeMs);
        }
    }
    
    /**
     * Log API request
     */
    public void apiRequest(String method, String endpoint) {
        logger.debug("API Request: {} {}", method, endpoint);
    }
    
    /**
     * Log API response
     */
    public void apiResponse(int statusCode, String description) {
        if (statusCode >= 200 && statusCode < 300) {
            logger.debug("API Response: {} - {}", statusCode, description);
        } else if (statusCode >= 400) {
            logger.warn("API Response: {} - {}", statusCode, description);
        } else {
            logger.debug("API Response: {} - {}", statusCode, description);
        }
    }
    
    /**
     * Log test step
     */
    public void step(String description) {
        logger.info("Step: {}", description);
    }
    
    /**
     * Log validation result
     */
    public void validation(String field, Object expected, Object actual, boolean passed) {
        if (passed) {
            logger.debug("Validation PASSED: {} - Expected: {}, Actual: {}", field, expected, actual);
        } else {
            logger.error("Validation FAILED: {} - Expected: {}, Actual: {}", field, expected, actual);
        }
    }
    
    /**
     * Log error with context
     */
    public void error(String message, Throwable throwable) {
        logger.error(message, throwable);
    }
    
    /**
     * Log warning
     */
    public void warn(String message, Object... args) {
        logger.warn(message, args);
    }
    
    /**
     * Log info message
     */
    public void info(String message, Object... args) {
        logger.info(message, args);
    }
    
    /**
     * Log debug message
     */
    public void debug(String message, Object... args) {
        logger.debug(message, args);
    }
}