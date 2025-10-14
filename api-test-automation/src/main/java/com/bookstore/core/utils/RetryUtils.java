package com.bookstore.core.utils;

import lombok.extern.slf4j.Slf4j;
import org.awaitility.Awaitility;
import org.awaitility.core.ConditionTimeoutException;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;

/**
 * Utility class for implementing retry mechanisms in tests.
 */
@Slf4j
public class RetryUtils {
    
    /**
     * Retries an operation until it succeeds or reaches the max attempts.
     *
     * @param operation The operation to retry
     * @param maxAttempts The maximum number of attempts
     * @param backoffMs The backoff time between retries in milliseconds
     * @param <T> The return type of the operation
     * @return The result of the operation
     * @throws Exception If all retry attempts fail
     */
    public static <T> T retry(Callable<T> operation, int maxAttempts, long backoffMs) throws Exception {
        int attempts = 0;
        Exception lastException = null;
        
        while (attempts < maxAttempts) {
            try {
                return operation.call();
            } catch (Exception e) {
                attempts++;
                lastException = e;
                
                if (attempts >= maxAttempts) {
                    break;
                }
                
                log.warn("Retry attempt {} of {} failed, waiting {}ms before next attempt. Error: {}", 
                        attempts, maxAttempts, backoffMs, e.getMessage());
                
                Thread.sleep(backoffMs);
            }
        }
        
        throw new RuntimeException("Operation failed after " + maxAttempts + " attempts", lastException);
    }
    
    /**
     * Waits for a condition to be met with a timeout.
     *
     * @param condition The condition to check
     * @param timeoutSeconds The timeout in seconds
     * @param pollIntervalMs The polling interval in milliseconds
     * @param description Description of what we're waiting for (for logging)
     */
    public static void waitFor(Callable<Boolean> condition, int timeoutSeconds, long pollIntervalMs, String description) {
        try {
            log.info("Waiting for: {}", description);
            Awaitility.await(description)
                    .atMost(timeoutSeconds, TimeUnit.SECONDS)
                    .pollInterval(pollIntervalMs, TimeUnit.MILLISECONDS)
                    .until(condition);
        } catch (ConditionTimeoutException e) {
            log.error("Timed out waiting for condition: {}", description);
            throw e;
        }
    }
    
    /**
     * Waits for an object to satisfy a condition with a timeout.
     *
     * @param supplier The supplier of the object to check
     * @param condition The condition to check
     * @param timeoutSeconds The timeout in seconds
     * @param pollIntervalMs The polling interval in milliseconds
     * @param description Description of what we're waiting for (for logging)
     * @param <T> The type of the object
     * @return The object that satisfies the condition
     */
    public static <T> T waitUntil(Callable<T> supplier, Predicate<T> condition, 
                                  int timeoutSeconds, long pollIntervalMs, String description) {
        try {
            log.info("Waiting until: {}", description);
            return Awaitility.await(description)
                    .atMost(timeoutSeconds, TimeUnit.SECONDS)
                    .pollInterval(pollIntervalMs, TimeUnit.MILLISECONDS)
                    .until(supplier, condition);
        } catch (ConditionTimeoutException e) {
            log.error("Timed out waiting for condition: {}", description);
            throw e;
        }
    }
}