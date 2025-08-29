package tn.sip.user_service.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;
import tn.sip.user_service.enums.UserRole;

@Data
public class UserUpdateRequest {
    private String firstName;
    private String lastName;
    private String email;
    private String phoneNumber;
    private UserRole role;
    private MultipartFile cinFile;
    private String agencyName;
    private MultipartFile rneFile;
    private MultipartFile patenteFile;
}
