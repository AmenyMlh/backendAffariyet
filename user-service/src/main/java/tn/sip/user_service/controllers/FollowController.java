package tn.sip.user_service.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.sip.user_service.services.FollowService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/follows")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:4200")
public class FollowController {
    private final FollowService followService;

    @PostMapping("/follow")
    public ResponseEntity<?> follow(@RequestParam Long userId, @RequestParam Long agencyId) {
        followService.followAgency(userId, agencyId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/unfollow")
    public ResponseEntity<Void> unfollow(@RequestParam Long userId, @RequestParam Long agencyId) {
        followService.unfollowAgency(userId, agencyId);
        return ResponseEntity.noContent().build();
    }



    @GetMapping("/is-following")
    public ResponseEntity<Boolean> isFollowing(@RequestParam Long userId, @RequestParam Long agencyId) {
        return ResponseEntity.ok(followService.isFollowing(userId, agencyId));
    }

    @GetMapping("/my-agencies")
    public ResponseEntity<List<Long>> getFollowedAgencies(@RequestParam Long userId) {
        return ResponseEntity.ok(followService.getFollowedAgencyIds(userId));
    }

    @GetMapping("/followers/count")
    public ResponseEntity<Map<String, Long>> getFollowersCount(@RequestParam Long agencyId) {
        Long count = followService.countFollowersByAgencyId(agencyId);
        Map<String, Long> response = new HashMap<>();
        response.put("count", count);
        return ResponseEntity.ok(response);
    }

}
