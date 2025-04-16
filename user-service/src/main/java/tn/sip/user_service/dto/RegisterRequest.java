package tn.sip.user_service.dto;
import lombok.Data;
import tn.sip.user_service.enums.UserRole;

@Data
public class RegisterRequest {

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String phoneNumber;

    private UserRole role;

    private String agencyName;

    private Long subscriptionId;


}
