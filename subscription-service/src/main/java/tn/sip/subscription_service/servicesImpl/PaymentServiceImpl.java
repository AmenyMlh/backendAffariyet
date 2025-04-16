package tn.sip.subscription_service.servicesImpl;

import lombok.RequiredArgsConstructor;
import org.apache.catalina.User;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import tn.sip.subscription_service.dto.AgencyDTO;
import tn.sip.subscription_service.dto.PaymentDTO;
import tn.sip.subscription_service.dto.UserDTO;
import tn.sip.subscription_service.entities.Payment;
import tn.sip.subscription_service.entities.Subscription;
import tn.sip.subscription_service.feigns.AgencyClient;
import tn.sip.subscription_service.repositories.PaymentRepository;
import tn.sip.subscription_service.repositories.SubscriptionRepository;
import tn.sip.subscription_service.services.EmailService;
import tn.sip.subscription_service.services.PaymentService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final AgencyClient agencyClient;
    private final SimpMessagingTemplate messagingTemplate;
    private final EmailService emailService;
    @Override
    public Payment createPayment(Payment payment) {
        if (payment.getStartDate() == null) {
            payment.setStartDate(LocalDate.now());
        }

        Subscription subscription = subscriptionRepository.findById(payment.getSubscriptionId())
                .orElseThrow(() -> new RuntimeException("Abonnement introuvable"));

        payment.calculateEndDate(subscription.getDurationInMonths());

        Payment savedPayment = paymentRepository.save(payment);
        int subscriptionName = subscription.getDurationInMonths();

        String agencyName;
        try {
            var agencyDTO = agencyClient.getAgencyById(savedPayment.getAgencyId());
            agencyName = (agencyDTO != null && agencyDTO.getAgencyName() != null)
                    ? agencyDTO.getAgencyName()
                    : "Agence inconnue";
        } catch (Exception e) {
            agencyName = "Agence inconnue";
        }

        String notificationMsg = "💸 Un nouveau paiement a été effectué pour l’abonnement de "
                + subscriptionName + " mois par l'agence " + agencyName + ".";

        messagingTemplate.convertAndSend("/topic/admin-notifications", notificationMsg);

        return savedPayment;
    }


    @Override
    public List<Payment> getPaymentsByAgency(Long agencyId) {
        return paymentRepository.findByAgencyId(agencyId);
    }

    @Override
    public List<Payment> getAllPayments() {
        return paymentRepository.findAll();
    }

    @Override
    public Optional<Payment> getPaymentById(Long id) {
        return paymentRepository.findById(id);
    }


    @Override
    public void deletePayment(Long id) {
        paymentRepository.deleteById(id);
    }
    @Override
    public List<PaymentDTO> getUnapprovedPayments() {
        return paymentRepository.findByIsApprovedFalse().stream().map(payment -> {
            PaymentDTO dto = new PaymentDTO();
            dto.setPaymentId(payment.getId());
            dto.setAmount(payment.getAmount());
            dto.setSubscriptionId(payment.getSubscriptionId());
            dto.setStartDate(payment.getStartDate());
            dto.setEndDate(payment.getEndDate());
            dto.setAttachment(payment.getAttachment());
            dto.setApproved(payment.isApproved());

            AgencyDTO agencyDTO = agencyClient.getAgencyById(payment.getAgencyId());
            dto.setAgency(agencyDTO);
            UserDTO userDTO = agencyClient.getUserById(agencyDTO.getUserId());
            dto.setUser(userDTO);

            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void approvePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé"));
        payment.setApproved(true);
        paymentRepository.save(payment);

        // Récupérer les informations de l'agence associée au paiement
        AgencyDTO agencyDTO = agencyClient.getAgencyById(payment.getAgencyId());
        UserDTO userDTO = agencyClient.getUserById(agencyDTO.getUserId());


        if (agencyDTO != null) {
            String notificationMessage = "💸 Votre paiement d'un montant de " + payment.getAmount() + " TND a été approuvé.";
               System.out.println(userDTO.getId());
            messagingTemplate.convertAndSend("/topic/agency-notifications/" + userDTO.getId(), notificationMessage);

            /*String emailSubject = "Paiement approuvé";
            String emailBody = "Bonjour " + agencyDTO.getAgencyName() + ",\n\n" +
                    "Votre paiement d'un montant de " + payment.getAmount() + " TND a été approuvé.\n" +
                    "Merci de votre collaboration.\n\nCordialement,\nL'équipe de gestion.";
             UserDTO userDTO = agencyClient.getUserById(agencyDTO.getUserId());
            emailService.sendEmail(userDTO.getEmail(), emailSubject, emailBody);*/
        }
    }
}
