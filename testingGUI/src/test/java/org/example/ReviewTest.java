package org.example;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)

//for commit

public class ReviewTest {

    private Review review;

    @Test
    @Order(1)
    @DisplayName("Test Default Constructor")
    void testDefaultConstructor() {
        review = new Review();
        assertNull(review.getReviewID(), "ReviewID should be null by default");
        assertNull(review.getUserID(), "UserID should be null by default");
        assertNull(review.getItemID(), "ItemID should be null by default");
        assertEquals(0, review.getRating(), "Rating should be 0 by default");
        assertNull(review.getComment(), "Comment should be null by default");
    }

    @Test
    @Order(2)
    @DisplayName("Test Parameterized Constructor")
    void testParameterizedConstructor() {
        review = new Review("U1", "I1", 5, "Great product!");

        assertNull(review.getReviewID(), "ReviewID should be null initially");
        assertEquals("U1", review.getUserID());
        assertEquals("I1", review.getItemID());
        assertEquals(5, review.getRating());
        assertEquals("Great product!", review.getComment());
    }

    @Test
    @Order(3)
    @DisplayName("Test setReviewID and getReviewID")
    void testReviewID() {
        review = new Review();
        review.setReviewID("R123");
        assertEquals("R123", review.getReviewID());
    }

    @Test
    @Order(4)
    @DisplayName("Test setUserID and getUserID")
    void testUserID() {
        review = new Review();
        review.setUserID("U99");
        assertEquals("U99", review.getUserID());
    }

    @Test
    @Order(5)
    @DisplayName("Test setItemID and getItemID")
    void testItemID() {
        review = new Review();
        review.setItemID("I42");
        assertEquals("I42", review.getItemID());
    }

    @Test
    @Order(6)
    @DisplayName("Test setRating and getRating")
    void testRating() {
        review = new Review();
        review.setRating(3);
        assertEquals(3, review.getRating());
    }

    @Test
    @Order(7)
    @DisplayName("Test setComment and getComment")
    void testComment() {
        review = new Review();
        review.setComment("Not bad!");
        assertEquals("Not bad!", review.getComment());
    }
}

