package tn.sip.reviewservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.sip.reviewservice.entities.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    List<Review> findByAgencyId(Long agencyId);

    boolean existsByReviewerIdAndAgencyIdAndAppointmentId(Long reviewerId, Long agencyId, Long appointmentId);
}
