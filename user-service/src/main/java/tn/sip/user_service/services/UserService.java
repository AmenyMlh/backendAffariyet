package tn.sip.user_service.services;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import tn.sip.user_service.dto.*;
import tn.sip.user_service.entities.User;

public interface UserService {

	List<User> getAllUsers();

	Optional<UserDTO>  getUserByEmail(String email);

	Optional<UserDTO> getUserById(Long id);

	//User updateUser(Long id, User updatedUser);

	void deleteUser(Long id);

	User createUser(User user, RegisterRequest registerRequest);

	DocumentsDTO getUserDocuments(String email);

	void sendVerificationCode(String email);

	boolean verifyCode(String email, String code);

	List<UserDTO> getNotApprovedUsersWithDocuments();

	void approveUser(Long userId);

	//User updateUser(Long userId, User updatedUser, Agency updatedAgency);

	User updateUser(Long userId, UserUpdateRequest request);

	void generateAndSendResetToken(String email);

	void resetPassword(String email, String resetToken, String newPassword);

	//User updateUserWithoutPass(Long userId, UserUpdateRequest request);

    void updateUserPassword(Long userId, String newPassword);

	String uploadProfilePicture(MultipartFile file, Long userId) throws IOException;

	boolean changePassword(Long userId, ChangePasswordRequest request);

    List<User> findAllAdmins();
}
