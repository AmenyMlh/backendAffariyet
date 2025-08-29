package tn.sip.user_service.dto;

import lombok.Data;
import tn.sip.user_service.enums.UserRole;

@Data
public class UserDTO {
    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String phoneNumber;

    private UserRole role;

    private boolean isEnabled;

    private boolean isApproved;

    private String cinFile;

    private String profilePicture;

    private AgencyDTO agencyDTO;
}
