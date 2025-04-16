package tn.sip.subscription_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.sip.subscription_service.entities.Subscription;

public interface SubscriptionRepository extends JpaRepository<Subscription, Long> {
}
