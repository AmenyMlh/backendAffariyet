package tn.sip.reviewservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.sip.reviewservice.entities.Review;
import tn.sip.reviewservice.services.ReviewService;

import java.util.List;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:4200")
public class ReviewController {

    private final ReviewService reviewService;

    @PostMapping
    public ResponseEntity<Void> addReview(@RequestBody Review dto) {
        reviewService.addReview(dto);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/agency/{agencyId}")
    public ResponseEntity<List<Review>> getReviews(@PathVariable Long agencyId) {
        return ResponseEntity.ok(reviewService.getReviewsForAgency(agencyId));
    }

    @GetMapping("/agency/{agencyId}/average")
    public ResponseEntity<Double> getAverageRating(@PathVariable Long agencyId) {
        return ResponseEntity.ok(reviewService.getAverageRating(agencyId));
    }

    @GetMapping("/exists")
    public ResponseEntity<Boolean> reviewExists(
            @RequestParam Long userId,
            @RequestParam Long agencyId,
            @RequestParam Long appointmentId) {

        boolean exists = reviewService.reviewExists(userId, agencyId, appointmentId);
        return ResponseEntity.ok(exists);
    }
}
