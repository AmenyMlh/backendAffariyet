package tn.sip.user_service.dto;

import lombok.Data;

@Data
public class VerificationRequest {
	private String email;
    private String code;
}
