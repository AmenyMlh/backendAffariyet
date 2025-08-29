package tn.sip.reviewservice.services;

import tn.sip.reviewservice.entities.Review;

import java.util.List;

public interface ReviewService {
    void addReview(Review review);
    List<Review> getReviewsForAgency(Long agencyId);
    double getAverageRating(Long agencyId);

    boolean reviewExists(Long reviewerId, Long agencyId, Long appointmentId);
}
