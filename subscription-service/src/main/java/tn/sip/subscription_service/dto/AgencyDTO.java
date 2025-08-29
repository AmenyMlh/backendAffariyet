package tn.sip.subscription_service.dto;

import lombok.Data;

@Data
public class AgencyDTO {

	   private Long id;

	    private String agencyName;

	    private String rneFile;

	    private String patenteFile;

	    private Long subscriptionId;

	    private boolean	isPaymentApproved;

	    private UserDTO user;
}
