package tn.sip.user_service.feigns;


import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tn.sip.user_service.entities.Subscription;

import java.util.List;

@FeignClient(name = "subscription-service", url = "http://localhost:8060")
public interface SubscriptionClient {
    @GetMapping("/api/subscriptions")
    List<Subscription> getAllSubscriptions();

    @GetMapping("/api/subscriptions/{id}")
    Subscription getSubscriptionById(@PathVariable Long id);

    @GetMapping("/api/payments/agency/{agencyId}")
    Subscription getPaymentByAgency(@PathVariable Long agencyId);
}
