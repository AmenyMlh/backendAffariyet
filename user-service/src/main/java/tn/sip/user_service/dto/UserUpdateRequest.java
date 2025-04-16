package tn.sip.user_service.dto;

import lombok.Data;
import tn.sip.user_service.enums.UserRole;

@Data
public class UserUpdateRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private String cinFile;
    private AgencyUpdateRequest agency;
}
