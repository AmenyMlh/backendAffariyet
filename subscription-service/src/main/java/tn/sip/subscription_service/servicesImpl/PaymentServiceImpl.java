package tn.sip.subscription_service.servicesImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.sip.subscription_service.dto.AgencyDTO;
import tn.sip.subscription_service.dto.NotificationRequest;
import tn.sip.subscription_service.dto.PaymentDTO;
import tn.sip.subscription_service.dto.UserDTO;
import tn.sip.subscription_service.entities.Payment;
import tn.sip.subscription_service.entities.Subscription;
import tn.sip.subscription_service.feigns.AgencyClient;
import tn.sip.subscription_service.feigns.NotificationClient;
import tn.sip.subscription_service.repositories.PaymentRepository;
import tn.sip.subscription_service.repositories.SubscriptionRepository;
import tn.sip.subscription_service.services.EmailService;
import tn.sip.subscription_service.services.PaymentService;

import java.io.IOException;
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
    private final NotificationClient notificationClient;
    private final EmailService emailService;
    private final FileStorageService fileStorageService;


    @Override
    public Payment createPayment(Payment payment, MultipartFile attachment) throws IOException {
        if (payment.getStartDate() == null) {
            payment.setStartDate(LocalDate.now());
        }

        Subscription subscription = subscriptionRepository.findById(payment.getSubscriptionId())
                .orElseThrow(() -> new RuntimeException("Abonnement introuvable"));

        payment.calculateEndDate(subscription.getDurationInMonths());

        // ✅ Utilisation correcte du fichier uploadé
        if (attachment != null && !attachment.isEmpty()) {
            String attachmentUrl = fileStorageService.saveFile(
                    "payment-attachments",
                    "/api/payments/files/payments/",
                    attachment
            );
            payment.setAttachment(attachmentUrl);
        }

        Payment savedPayment = paymentRepository.save(payment);

        int subscriptionName = subscription.getDurationInMonths();
        var agencyDTO = agencyClient.getAgencyById(savedPayment.getAgencyId());
        String agencyName;
        try {
            agencyName = (agencyDTO != null && agencyDTO.getAgencyName() != null)
                    ? agencyDTO.getAgencyName()
                    : "Agence inconnue";
        } catch (Exception e) {
            agencyName = "Agence inconnue";
        }

        String notificationMsg = "💸 Un nouveau paiement a été effectué pour l’abonnement de "
                + subscriptionName + " mois par l'agence " + agencyName + ".";

        notificationClient.sendNotification(NotificationRequest.builder()
                .message(notificationMsg)
                .userId(agencyDTO.getUser().getId())
                .seen(false)
                .url("/topic/admin-notifications")
                .build());

        List<UserDTO> adminUsers = agencyClient.getAllAdmins();
        for (UserDTO admin : adminUsers) {
            if (admin.getEmail() != null && !admin.getEmail().isEmpty()) {
                emailService.sendEmail(
                        admin.getEmail(),
                        "Notification de Paiement",
                        "Un nouveau paiement a été effectué pour l’abonnement de "
                                + subscriptionName + " mois par l'agence " + agencyName + "."
                );
            }
        }

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
            if (agencyDTO.getUser() != null && agencyDTO.getUser().getId() != null) {
                UserDTO userDTO = agencyClient.getUserById(agencyDTO.getUser().getId());
                dto.setUser(userDTO);
            }


            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public void approvePayment(Long paymentId) {
        Payment payment = paymentRepository.findById(paymentId)
                .orElseThrow(() -> new RuntimeException("Paiement non trouvé"));
        payment.setApproved(true);
        paymentRepository.save(payment);

        AgencyDTO agencyDTO = agencyClient.getAgencyById(payment.getAgencyId());
        UserDTO userDTO = agencyClient.getUserById(agencyDTO.getUser().getId());

        agencyDTO.setPaymentApproved(true);
        agencyClient.approvePayment(payment.getAgencyId(), payment.isApproved());


        if (agencyDTO != null) {
            String notificationMessage = "💸 Votre paiement d'un montant de " + payment.getAmount() + " TND a été approuvé.";
            notificationClient.sendNotification(
                    NotificationRequest.builder()
                            .userId(userDTO.getId())
                            .message(notificationMessage)
                            .seen(false)
                            .url("/topic/agency-notifications/" + userDTO.getId())
                            .build()
            );


            //messagingTemplate.convertAndSend("/topic/agency-notifications/" + userDTO.getId(), notificationMessage);
            emailService.sendEmail(
                    userDTO.getEmail(),
                    "Confirmation de paiement approuvé",
                    "Bonjour,\n\nNous vous informons que votre paiement d'un montant de " + payment.getAmount() +
            " TND a été approuvé avec succès.\n\nMerci pour votre confiance.\n\nL'équipe Support."
            );
        }
    }
}
