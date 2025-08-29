package tn.sip.user_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.sip.user_service.entities.Follow;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, Long> {
    boolean existsByUserIdAndAgencyId(Long userId, Long agencyId);
    void deleteByUserIdAndAgencyId(Long userId, Long agencyId);
    List<Follow> findByUserId(Long userId);
    Long countByAgencyId(Long agencyId);
}
