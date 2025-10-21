package base;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.TestLogger;
import io.restassured.RestAssured;

/**
 * Base test class with simplified logging and MDC support for parallel tests
 */
public abstract class BaseE2ETest {
    
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected final TestLogger testLogger = new TestLogger(this.getClass());
    private long testStartTime;
    private String currentTestId;
    
    @BeforeEach
    void baseSetUp(TestInfo testInfo) {
        // Record test start time
        testStartTime = System.currentTimeMillis();
        
        // Setup test context for MDC
        String testName = testInfo.getDisplayName();
        String testMethod = testInfo.getTestMethod().map(method -> method.getName()).orElse("unknown");
        currentTestId = extractTestId(testMethod);
        
        testLogger.setupTestContext(testName);
        testLogger.testStart(currentTestId, testName);
        
        // Configure RestAssured
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/api";
        
        logger.debug("Test environment configured - Base URI: {}:{}{}", 
                RestAssured.baseURI, RestAssured.port, RestAssured.basePath);
    }
    
    @AfterEach
    void baseTearDown(TestInfo testInfo) {
        // Calculate execution time
        long executionTime = System.currentTimeMillis() - testStartTime;
        
        // Determine if test passed (simplified - could be enhanced with TestResult)
        boolean testPassed = true; // This would need to be determined from test result
        
        // Log test completion
        testLogger.testComplete(currentTestId, testPassed, executionTime);
        
        // Clear MDC context
        testLogger.clearTestContext();
        
        // Reset RestAssured
        RestAssured.reset();
    }
    
    /**
     * Extract test ID from method name (e.g., testOrderPlacement_O_E2E_001 -> O-E2E-001)
     */
    private String extractTestId(String methodName) {
        if (methodName.contains("_")) {
            String[] parts = methodName.split("_");
            if (parts.length >= 4) {
                return String.join("-", parts[parts.length - 3], parts[parts.length - 2], parts[parts.length - 1]);
            }
        }
        return methodName;
    }
    
    /**
     * Log test step
     */
    protected void logStep(String description) {
        testLogger.step(description);
    }
    
    /**
     * Log validation result
     */
    protected void logValidation(String field, Object expected, Object actual, boolean passed) {
        testLogger.validation(field, expected, actual, passed);
    }
    
    /**
     * Log API request
     */
    protected void logApiRequest(String method, String endpoint) {
        testLogger.apiRequest(method, endpoint);
    }
    
    /**
     * Log API response
     */
    protected void logApiResponse(int statusCode, String description) {
        testLogger.apiResponse(statusCode, description);
    }
    
    /**
     * Log error with context
     */
    protected void logError(String message, Throwable throwable) {
        testLogger.error(message, throwable);
    }
    
    /**
     * Log warning
     */
    protected void logWarning(String message, Object... args) {
        testLogger.warn(message, args);
    }
    
    /**
     * Log info message
     */
    protected void logInfo(String message, Object... args) {
        testLogger.info(message, args);
    }
    
    /**
     * Log debug message
     */
    protected void logDebug(String message, Object... args) {
        testLogger.debug(message, args);
    }
}