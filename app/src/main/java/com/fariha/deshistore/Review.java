package com.fariha.deshistore;

import java.util.Date;

public class Review {
    private String id;
    private String productId;
    private String reviewerName;
    private String userId;
    private int rating;
    private String reviewText;
    private Date date;

    // Default constructor
    public Review() {
    }

    // Full constructor
    public Review(String id, String productId, String reviewerName, String userId, 
                  int rating, String reviewText, Date date) {
        this.id = id;
        this.productId = productId;
        this.reviewerName = reviewerName;
        this.userId = userId;
        this.rating = rating;
        this.reviewText = reviewText;
        this.date = date;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public String getReviewerName() {
        return reviewerName;
    }

    public String getUserId() {
        return userId;
    }

    public int getRating() {
        return rating;
    }

    public String getReviewText() {
        return reviewText;
    }

    public Date getDate() {
        return date;
    }

    // Setters
    public void setId(String id) {
        this.id = id;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public void setReviewerName(String reviewerName) {
        this.reviewerName = reviewerName;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
