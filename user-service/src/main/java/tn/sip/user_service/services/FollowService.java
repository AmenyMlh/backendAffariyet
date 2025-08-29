package tn.sip.user_service.services;

import java.util.List;

public interface FollowService {
    void followAgency(Long userId, Long agencyId);

    void unfollowAgency(Long userId, Long agencyId);

    boolean isFollowing(Long userId, Long agencyId);

    List<Long> getFollowedAgencyIds(Long userId);

    Long countFollowersByAgencyId(Long agencyId);
}
