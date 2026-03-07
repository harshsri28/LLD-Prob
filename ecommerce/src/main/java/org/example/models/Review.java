package org.example.models;

import java.util.Date;
import java.util.UUID;

public class Review {
    private String reviewId;
    private String productId;
    private String memberId;
    private int rating; // 1 to 5
    private String comment;
    private Date createdDate;

    public Review(String productId, String memberId, int rating, String comment) {
        this.reviewId = "REV-" + UUID.randomUUID().toString().substring(0, 8);
        this.productId = productId;
        this.memberId = memberId;
        this.rating = Math.max(1, Math.min(5, rating)); // Clamp between 1-5
        this.comment = comment;
        this.createdDate = new Date();
    }

    public String getReviewId() { return reviewId; }
    public String getProductId() { return productId; }
    public String getMemberId() { return memberId; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }
    public Date getCreatedDate() { return createdDate; }

    @Override
    public String toString() {
        return "Review{rating=" + rating + "/5, comment='" + comment + "', by=" + memberId + "}";
    }
}
