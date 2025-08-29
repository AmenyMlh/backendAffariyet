package tn.sip.user_service.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AgencyUpdateRequest {
    private String agencyName;
    private MultipartFile rneFile;
    private MultipartFile patenteFile;
    private Long subscriptionId;
    private boolean isPaymentApproved;
}
