package tn.sip.reviewservice.servicesImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.sip.reviewservice.entities.Review;
import tn.sip.reviewservice.repositories.ReviewRepository;
import tn.sip.reviewservice.services.ReviewService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    @Override
    public void addReview(Review dto) {
        boolean alreadyReviewed = reviewRepository.existsByReviewerIdAndAgencyIdAndAppointmentId(dto.getReviewerId(), dto.getAgencyId(), dto.getAppointmentId());

        if (alreadyReviewed) {
            throw new IllegalStateException("Vous avez déjà laissé un avis pour cette visite.");
        }

        Review review = Review.builder()
                .rating(dto.getRating())
                .comment(dto.getComment())
                .reviewerId(dto.getReviewerId())
                .agencyId(dto.getAgencyId())
                .appointmentId(dto.getAppointmentId())
                .createdAt(LocalDateTime.now())
                .build();

        reviewRepository.save(review);
    }


    @Override
    public List<Review> getReviewsForAgency(Long agencyId) {
        return reviewRepository.findByAgencyId(agencyId);
    }

    @Override
    public double getAverageRating(Long agencyId) {
        List<Review> reviews = reviewRepository.findByAgencyId(agencyId);
        return reviews.isEmpty() ? 0.0 : reviews.stream().mapToInt(Review::getRating).average().orElse(0.0);
    }

    @Override
    public boolean reviewExists(Long reviewerId, Long agencyId, Long appointmentId) {
        return reviewRepository.existsByReviewerIdAndAgencyIdAndAppointmentId(reviewerId, agencyId, appointmentId);
    }


}
