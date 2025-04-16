package tn.sip.subscription_service.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class PaymentDTO {
    private Long paymentId;
    private Long subscriptionId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double amount;
    private String attachment;
    private boolean isApproved;
    private AgencyDTO agency;
    private UserDTO user;
}
