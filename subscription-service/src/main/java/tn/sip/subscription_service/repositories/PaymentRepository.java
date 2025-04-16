package tn.sip.subscription_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.sip.subscription_service.entities.Payment;

import java.util.Collection;
import java.util.List;

public interface PaymentRepository extends JpaRepository<Payment, Long> {
    List<Payment> findByAgencyId(Long agencyId);
    List<Payment> findBySubscriptionId(Long subscriptionId);

    List<Payment> findByIsApprovedFalse();
}
