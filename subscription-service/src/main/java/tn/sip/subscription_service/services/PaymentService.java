package tn.sip.subscription_service.services;

import org.springframework.web.multipart.MultipartFile;
import tn.sip.subscription_service.dto.PaymentDTO;
import tn.sip.subscription_service.entities.Payment;
import tn.sip.subscription_service.entities.Subscription;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public interface PaymentService {

    Payment createPayment(Payment payment, MultipartFile attachment) throws IOException;

    List<Payment> getPaymentsByAgency(Long agencyId);
    List<Payment> getAllPayments();
    Optional<Payment> getPaymentById(Long id);

    void deletePayment(Long id);

    List<PaymentDTO> getUnapprovedPayments();

    void approvePayment(Long paymentId);
}
