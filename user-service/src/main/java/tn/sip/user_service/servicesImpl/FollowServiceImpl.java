package tn.sip.user_service.servicesImpl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.sip.user_service.entities.Follow;
import tn.sip.user_service.repositories.FollowRepository;
import tn.sip.user_service.services.FollowService;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FollowServiceImpl implements FollowService {
    private final FollowRepository followRepository;

    @Override
    public void followAgency(Long userId, Long agencyId) {
        if (!followRepository.existsByUserIdAndAgencyId(userId, agencyId)) {
            followRepository.save(new Follow(null, userId, agencyId, LocalDateTime.now()));
        }
    }

    @Override
    @Transactional
    public void unfollowAgency(Long userId, Long agencyId) {
        followRepository.deleteByUserIdAndAgencyId(userId, agencyId);
    }

    @Override
    public boolean isFollowing(Long userId, Long agencyId) {
        return followRepository.existsByUserIdAndAgencyId(userId, agencyId);
    }

    @Override
    public List<Long> getFollowedAgencyIds(Long userId) {
        return followRepository.findByUserId(userId)
                .stream()
                .map(Follow::getAgencyId)
                .toList();
    }

    @Override
    public Long countFollowersByAgencyId(Long agencyId) {
        return followRepository.countByAgencyId(agencyId);
    }
}
