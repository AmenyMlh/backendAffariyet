package tn.sip.subscription_service.services;

import tn.sip.subscription_service.entities.Subscription;

import java.util.List;

public interface SubscriptionService {
    List<Subscription> getAllSubscriptions();
    Subscription getSubscriptionById(Long id);
    Subscription createSubscription(Subscription subscription);
    Subscription updateSubscription(Long id, Subscription subscription);
    boolean deleteSubscription(Long id);
}
