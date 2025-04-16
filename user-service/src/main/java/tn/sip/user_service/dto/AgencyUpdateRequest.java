package tn.sip.user_service.dto;

import lombok.Data;

@Data
public class AgencyUpdateRequest {
    private String agencyName;
    private String rneFile;
    private String patenteFile;
    private Long subscriptionId;
    private boolean isPaymentApproved;
}
