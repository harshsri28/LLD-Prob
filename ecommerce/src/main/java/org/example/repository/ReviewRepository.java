package org.example.repository;

import org.example.models.Review;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ReviewRepository {
    private Map<String, Review> reviews = new HashMap<>();

    public void addReview(Review review) {
        reviews.put(review.getReviewId(), review);
    }

    public List<Review> getReviewsByProductId(String productId) {
        return reviews.values().stream()
                .filter(r -> r.getProductId().equals(productId))
                .collect(Collectors.toList());
    }

    public List<Review> getReviewsByMemberId(String memberId) {
        return reviews.values().stream()
                .filter(r -> r.getMemberId().equals(memberId))
                .collect(Collectors.toList());
    }

    public List<Review> getAllReviews() {
        return new ArrayList<>(reviews.values());
    }
}
