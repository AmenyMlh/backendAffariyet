package tn.sip.user_service.dto;

import lombok.Data;
import tn.sip.user_service.enums.UserRole;

@Data
public class LoginResponse {

	private Long id;
	private String firstName;
    private String lastName;
    private String agencyName;
    private String email;
    private UserRole role;
    private String jwt;
    private String refreshToken;
    private DocumentsDTO missingDocuments;
    private boolean isApproved ;

}
