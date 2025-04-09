package org.example;

import com.google.cloud.firestore.annotation.PropertyName;

public class Review {
    private String ItemID;
    private String ReviewID;
    private String UserID;
    private int rating;
    private String Comment;

    public Review() {}
    public Review(String UserId,String ItemID ,int rating, String Comment) {
        this.ReviewID = null;
        this.UserID = UserId;
        this.ItemID = ItemID;
        this.rating = rating;
        this.Comment = Comment;
    }


    public String getItemID() {
        return ItemID;
    }

    public void setItemID(String itemID) {
        ItemID = itemID;
    }

    @PropertyName("ReviewID")
    public String getReviewID() {
        return ReviewID;
    }

    @PropertyName("ReviewID")
    public void setReviewID(String reviewId) {
        ReviewID = reviewId;
    }

    public String getUserID() {
        return UserID;
    }

    public void setUserID(String userId) {
        UserID = userId;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return Comment;
    }

    public void setComment(String comment) {
        Comment = comment;
    }
}
