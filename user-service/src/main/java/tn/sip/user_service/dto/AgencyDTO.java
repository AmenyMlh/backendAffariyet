package tn.sip.user_service.dto;

import lombok.Data;

@Data
public class AgencyDTO {

	   private Long id;

	    private String agencyName;

	    private String rneFile;

	    private String patenteFile;

	    private Long subscriptionId;

	    private Long userId;
}
