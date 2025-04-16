package tn.sip.subscription_service.services;

import tn.sip.subscription_service.dto.PaymentDTO;
import tn.sip.subscription_service.entities.Payment;
import tn.sip.subscription_service.entities.Subscription;

import java.util.List;
import java.util.Optional;

public interface PaymentService {
    Payment createPayment(Payment payment);
    List<Payment> getPaymentsByAgency(Long agencyId);
    List<Payment> getAllPayments();
    Optional<Payment> getPaymentById(Long id);

    void deletePayment(Long id);

    List<PaymentDTO> getUnapprovedPayments();

    void approvePayment(Long paymentId);
}
