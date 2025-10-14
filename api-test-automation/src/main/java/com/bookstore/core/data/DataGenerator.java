package com.bookstore.core.data;

import com.github.javafaker.Faker;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Utility for generating test data.
 */
@Slf4j
public class DataGenerator {
    private static final Faker faker = new Faker();
    
    /**
     * Generates a random book name.
     *
     * @return A random book name
     */
    public static String generateBookName() {
        return faker.book().title();
    }
    
    /**
     * Generates a random book description.
     *
     * @return A random book description
     */
    public static String generateBookDescription() {
        return faker.lorem().paragraph(2);
    }
    
    /**
     * Generates a random book price.
     *
     * @return A random book price between 5.00 and 50.00
     */
    public static BigDecimal generateBookPrice() {
        double price = 5.0 + ThreadLocalRandom.current().nextDouble(45.0);
        return BigDecimal.valueOf(price).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * Generates a random author name.
     *
     * @return A random author name
     */
    public static String generateAuthorName() {
        return faker.name().fullName();
    }
    
    /**
     * Generates a random author bio.
     *
     * @return A random author bio
     */
    public static String generateAuthorBio() {
        return faker.lorem().paragraph(3);
    }
    
    /**
     * Generates a random SKU code.
     *
     * @return A random SKU code
     */
    public static String generateSkuCode() {
        return "SKU-" + faker.numerify("#####");
    }
    
    /**
     * Generates a random quantity.
     *
     * @return A random quantity between 1 and 10
     */
    public static int generateQuantity() {
        return ThreadLocalRandom.current().nextInt(1, 11);
    }
    
    /**
     * Generates a random order number.
     *
     * @return A random order number
     */
    public static String generateOrderNumber() {
        return "ORD-" + faker.numerify("######");
    }
    
    /**
     * Generates a random UUID.
     *
     * @return A random UUID as a string
     */
    public static String generateUuid() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * Generates a random email address.
     *
     * @return A random email address
     */
    public static String generateEmail() {
        return faker.internet().emailAddress();
    }
}