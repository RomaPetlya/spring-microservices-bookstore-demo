package base;

import utils.TestLogger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.TestInfo;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;

/**
 * Base test class that provides common functionality and logging for all E2E tests
 */
public abstract class BaseE2ETest {
    
    protected TestLogger log;
    private long testStartTime;
    
    @BeforeEach
    void baseSetUp(TestInfo testInfo) {
        // Initialize logger for the specific test class
        log = new TestLogger(this.getClass());
        
        // Record test start time
        testStartTime = System.currentTimeMillis();
        
        // Log test start
        String testName = testInfo.getDisplayName();
        String testMethod = testInfo.getTestMethod().map(method -> method.getName()).orElse("unknown");
        log.testStart(testMethod, testName);
        
        // Configure RestAssured logging
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        
        // Set base configuration
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
        RestAssured.basePath = "/api";
        
        log.info("Test environment configured - Base URI: {}:{}{}", 
                RestAssured.baseURI, RestAssured.port, RestAssured.basePath);
    }
    
    @AfterEach
    void baseTearDown(TestInfo testInfo) {
        // Calculate execution time
        long executionTime = System.currentTimeMillis() - testStartTime;
        
        // Log test completion
        String testMethod = testInfo.getTestMethod().map(method -> method.getName()).orElse("unknown");
        log.testSummary(testMethod, executionTime, "COMPLETED");
        
        // Reset RestAssured
        RestAssured.reset();
    }
    
    /**
     * Log successful test completion with details
     */
    protected void logTestSuccess(String testId, String details) {
        log.testPass(testId, details);
    }
    
    /**
     * Log test failure with error details
     */
    protected void logTestFailure(String testId, String error) {
        log.testFail(testId, error);
    }
    
    /**
     * Log API request for tracking
     */
    protected void logApiRequest(String method, String endpoint, String payload) {
        log.apiRequest(method, endpoint, payload);
    }
    
    /**
     * Log API response for tracking
     */
    protected void logApiResponse(int statusCode, String response) {
        log.apiResponse(statusCode, response);
    }
    
    /**
     * Log workflow step execution
     */
    protected void logWorkflowStep(int step, String description) {
        log.workflowStep(step, description);
    }
    
    /**
     * Log workflow step completion
     */
    protected void logWorkflowComplete(int step, String result) {
        log.workflowStepComplete(step, result);
    }
    
    /**
     * Log assertion results with clear pass/fail indication
     */
    protected void logAssertion(String description, boolean passed, String details) {
        log.assertion(description, passed, details);
    }
    
    /**
     * Log validation results
     */
    protected void logValidation(String field, String expected, String actual, boolean passed) {
        log.validation(field, expected, actual, passed);
    }
    
    /**
     * Log cleanup operations
     */
    protected void logCleanup(String operation, String details) {
        log.cleanup(operation, details);
    }
}