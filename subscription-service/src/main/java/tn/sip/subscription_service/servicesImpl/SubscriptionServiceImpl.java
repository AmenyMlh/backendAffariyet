package tn.sip.subscription_service.servicesImpl;

import org.springframework.stereotype.Service;
import tn.sip.subscription_service.dto.PaymentDTO;
import tn.sip.subscription_service.entities.Subscription;
import tn.sip.subscription_service.repositories.SubscriptionRepository;
import tn.sip.subscription_service.services.SubscriptionService;

import java.util.List;
import java.util.Optional;

@Service
public class SubscriptionServiceImpl implements SubscriptionService {

    private final SubscriptionRepository subscriptionRepository;

    public SubscriptionServiceImpl(SubscriptionRepository subscriptionRepository) {
        this.subscriptionRepository = subscriptionRepository;
    }

    @Override
    public List<Subscription> getAllSubscriptions() {
        return subscriptionRepository.findAll();
    }

    @Override
    public Subscription getSubscriptionById(Long id) {
        Optional<Subscription> subscription = subscriptionRepository.findById(id);
        return subscription.orElse(null);
    }


    @Override
    public Subscription createSubscription(Subscription subscription) {
        return subscriptionRepository.save(subscription);
    }

    @Override
    public Subscription updateSubscription(Long id, Subscription subscription) {
        if (subscriptionRepository.existsById(id)) {
            subscription.setId(id);
            return subscriptionRepository.save(subscription);
        }
        return null;
    }

    @Override
    public boolean deleteSubscription(Long id) {
        if (subscriptionRepository.existsById(id)) {
            subscriptionRepository.deleteById(id);
            return true;
        }
        return false;
    }

}
