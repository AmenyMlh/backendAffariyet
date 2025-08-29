package tn.sip.user_service.servicesImpl;

import java.io.IOException;
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.web.multipart.MultipartFile;
import tn.sip.user_service.dto.*;
import tn.sip.user_service.entities.Agency;
import tn.sip.user_service.entities.Subscription;
import tn.sip.user_service.entities.Token;
import tn.sip.user_service.entities.User;
import tn.sip.user_service.enums.TokenType;
import tn.sip.user_service.enums.UserRole;
import tn.sip.user_service.feigns.SubscriptionClient;
import tn.sip.user_service.mappers.AgencyMapper;
import tn.sip.user_service.mappers.UserMapper;
import tn.sip.user_service.repositories.AgencyRepository;
import tn.sip.user_service.repositories.TokenRepository;
import tn.sip.user_service.repositories.UserRepository;
import tn.sip.user_service.services.AgencyService;
import tn.sip.user_service.services.EmailService;
import tn.sip.user_service.services.UserService;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
	@Value("${front.url}")
    private String frontUrl;
    //@Value("${file.uploads.photos-output-path}")
    //private String uploadsDirectory;
    private final FileStorageService fileStorageService;
	private final UserRepository userRepository;
    private final EmailService emailService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final AgencyService agencyService;
	@Autowired
	private AgencyRepository agencyRepository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    @Autowired
    private SubscriptionClient subscriptionClient;
    private static final long EXPIRY_TIME = 15 * 60 * 1000;
    @Autowired
    private TokenRepository tokenRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public String getFrontUrl() {
        return frontUrl;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

     @Override
     public Optional<UserDTO> getUserById(Long id) {
         Optional<User> userOptional = userRepository.findById(id);

         if (userOptional.isPresent()) {
             User user = userOptional.get();
             UserDTO userDTO = UserMapper.INSTANCE.toUserDTO(user);

             if (user.getRole() == UserRole.AGENCY) {
                 Agency agency = agencyRepository.findByUser(user);
                 if (agency != null) {
                     userDTO.setAgencyDTO(AgencyMapper.INSTANCE.toAgencyDTO(agency));
                 }

             }

             return Optional.of(userDTO);
         }

         return Optional.empty();
     }



    @Override
    public Optional<UserDTO> getUserByEmail(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return Optional.empty();
        }

        UserDTO dto = new UserDTO();
        dto.setId(user.getId());
        dto.setFirstName(user.getFirstName());
        dto.setLastName(user.getLastName());
        dto.setEmail(user.getEmail());
        dto.setPhoneNumber(user.getPhoneNumber());
        dto.setRole(user.getRole());
        dto.setCinFile(user.getCinFile());
        dto.setProfilePicture(user.getProfilePicture());
        dto.setEnabled(user.isEnabled());
        dto.setApproved(user.isApproved());

        if (user.getRole() == UserRole.AGENCY) {
            Agency agency = agencyRepository.findByUser(user);
            if (agency != null) {
                dto.setAgencyDTO(AgencyMapper.INSTANCE.toAgencyDTO(agency));
            }
        }

        return Optional.of(dto);
    }



    @Override
    @Transactional
    public void sendVerificationCode(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Utilisateur introuvable avec l'email : " + email);
        }

        Token verificationToken = generateToken(user, TokenType.VERIFICATION);

        String subject = "Vérification de votre compte";
        String message = "Bonjour,\n\n"
                + "Veuillez cliquer sur le lien suivant pour confirmer votre compte :\n"
                + getFrontUrl() + "/auth/verify-code?verificationCode=" + verificationToken.getToken() + "&email=" + email
                + "\nCe code expirera dans " + (EXPIRY_TIME / 60000) + " minutes.\n\n"
                + "Cordialement,\nL'équipe Support";

        emailService.sendEmail(user.getEmail(), subject, message);
    }


    @Override
    @Transactional
    public boolean verifyCode(String email, String verificationCode) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return false;
        }
        Token token = tokenRepository.findByTokenAndTokenType(verificationCode, TokenType.VERIFICATION)
                .orElse(null);

        if (token == null || token.isExpired()) {
            return false;
        }
        user.setEnabled(true);
        userRepository.save(user);
        tokenRepository.delete(token);

        return true;
    }


    @Override
    @Transactional
    public User createUser(User user, RegisterRequest registerRequest) {

        User existingUser = userRepository.findByEmail(user.getEmail());
        if (existingUser != null) {
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà !");
        }

        user.setEnabled(false);
        User createdUser = userRepository.save(user);

        Token verificationToken = generateToken(createdUser, TokenType.VERIFICATION);
        emailService.sendEmailWithVerificationCode(user.getEmail(), verificationToken.getToken());

        if (registerRequest.getRole().equals(UserRole.AGENCY)) {
            Subscription subscription = subscriptionClient.getSubscriptionById(registerRequest.getSubscriptionId());

            Agency agency = new Agency();
            agency.setAgencyName(registerRequest.getAgencyName());
            agency.setUser(createdUser);
            agency.setSubscriptionId(registerRequest.getSubscriptionId());

            agency.setStartDate(LocalDate.now());
            agency.setEndDate(agency.getStartDate().plusMonths(subscription.getDurationInMonths()));

            agencyService.saveAgency(agency);
        }

        return createdUser;
    }


    private Token generateToken(User user, TokenType tokenType) {
        SecureRandom random = new SecureRandom();
        String tokenValue = String.valueOf(100000 + random.nextInt(900000)); // 6-digit token
        long expiryTime = System.currentTimeMillis() + EXPIRY_TIME;

        Token token = new Token();
        token.setToken(tokenValue);
        token.setExpiryTime(expiryTime);
        token.setTokenType(tokenType);
        token.setUser(user);

        return tokenRepository.save(token);
    }


    @Override
    @Transactional
    public User updateUser(Long userId, UserUpdateRequest request) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        existingUser.setFirstName(request.getFirstName());
        existingUser.setLastName(request.getLastName());
        existingUser.setEmail(request.getEmail());
        existingUser.setPhoneNumber(request.getPhoneNumber());

        // 📁 Sauvegarde du fichier CIN
        if (request.getCinFile() != null && !request.getCinFile().isEmpty()) {
            try {
                String cinUrl = fileStorageService.saveFile("cin", "/api/user/users/files/cin/", request.getCinFile());
                existingUser.setCinFile(cinUrl);
            } catch (IOException e) {
                throw new RuntimeException("Erreur lors du téléchargement du fichier CIN", e);
            }
        }

        // 🏢 Mise à jour agence si nécessaire
        if (existingUser.getRole() == UserRole.AGENCY) {
            Agency existingAgency = agencyRepository.findByUser(existingUser);

            if (existingAgency != null) {
                existingAgency.setAgencyName(request.getAgencyName());

                try {
                    if (request.getRneFile() != null && !request.getRneFile().isEmpty()) {
                        String rneUrl = fileStorageService.saveFile("rne", "/api/user/users/files/rne/", request.getRneFile());
                        existingAgency.setRneFile(rneUrl);
                    }

                    if (request.getPatenteFile() != null && !request.getPatenteFile().isEmpty()) {
                        String patenteUrl = fileStorageService.saveFile("patente", "/api/user/users/files/patente/", request.getPatenteFile());
                        existingAgency.setPatenteFile(patenteUrl);
                    }
                } catch (IOException e) {
                    throw new RuntimeException("Erreur lors du téléchargement des fichiers agence", e);
                }

                agencyRepository.save(existingAgency);
            }
        }


        return userRepository.save(existingUser);
    }



    @Transactional
    @Override
    public void updateUserPassword(Long userId, String newPassword) {
        User existingUser = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (newPassword == null || newPassword.isBlank()) {
            throw new IllegalArgumentException("Le mot de passe ne peut pas être vide.");
        }

        // Encodage du mot de passe
        String encodedPassword = passwordEncoder.encode(newPassword);
        existingUser.setPassword(encodedPassword);

        userRepository.save(existingUser);
    }

    @Override
    public String uploadProfilePicture(MultipartFile file, Long userId) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with id: " + userId));

        if (user.getProfilePicture() != null && !user.getProfilePicture().isEmpty()) {
            String oldFileName = fileStorageService.extractFileNameFromUrl(user.getProfilePicture());
            fileStorageService.deleteFile("profilePictures", oldFileName);
        }
        String fileUrl = fileStorageService.saveFile("profilePictures", "api/user/users/profilePicture/", file);
        user.setProfilePicture(fileUrl);
        userRepository.save(user);

        return fileUrl;
    }



    @Transactional
    @Override
    public boolean changePassword(Long userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        if (!bCryptPasswordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            return false;
        }

        if (!request.getNewPassword().equals(request.getConfirmPassword())) {
            return false;
        }

        user.setPassword(bCryptPasswordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        return true;
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("Utilisateur non trouvé avec l'ID : " + id);
        }
        userRepository.deleteById(id);
    }

	@Override
	public DocumentsDTO getUserDocuments(String email) {
		User user= userRepository.findByEmail(email);
		DocumentsDTO documents = new DocumentsDTO();
		UserRole userRole = user.getRole();
		if (user.getCinFile()==null) {
			documents.setMessage("Veuillez Completez les documents");
			return documents;
		}
		else {
			if (userRole.equals(UserRole.INDIVIDUAL)) {
				documents.setCinFile(user.getCinFile());
				return documents;
			}
			else {
				if (userRole.equals(UserRole.AGENCY)) {
					documents.setCinFile(user.getCinFile());
					return agencyService.getAgencyDocumentsByUserId(documents,user);
				}
			}
		}
		return documents;
	}

    @Override
    public List<UserDTO> getNotApprovedUsersWithDocuments() {
        List<User> users = userRepository.findByIsApprovedFalseAndRole(UserRole.AGENCY);
        List<UserDTO> userDTOs = new ArrayList<>();

        for (User user : users) {
            UserDTO userDTO = UserMapper.INSTANCE.toUserDTO(user);

            if (user.getRole() == UserRole.AGENCY) {
                Agency agency = agencyRepository.findByUser(user);
                if (agency != null) {
                    userDTO.setAgencyDTO(AgencyMapper.INSTANCE.toAgencyDTO(agency));
                }
            }

            userDTOs.add(userDTO);
        }

        return userDTOs;
    }


    @Override
        public void approveUser(Long userId) {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("Utilisateur non trouvé avec l'ID : " + userId));

            user.setApproved(true);
            userRepository.save(user);
            messagingTemplate.convertAndSend("/topic/agency-notifications/"+userId, "Votre compte a été approuvé !");
        emailService.sendEmail(
                user.getEmail(),
                "Approbation de votre compte",
                "Bonjour,\n\nNous avons le plaisir de vous informer que votre compte a été approuvé suite à la validation de vos documents.\n\nMerci pour votre confiance.\n\nL'équipe Support."
        );

    }

    @Override
    @Transactional
    public void generateAndSendResetToken(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Utilisateur introuvable avec l'email : " + email);
        }

        Token resetToken = generateToken(user, TokenType.RESET_PASSWORD);

        String resetLink = "http://localhost:4200/auth/reset-password?email=" + email + "&resetToken=" + resetToken.getToken();

        String subject = "Réinitialisation de votre mot de passe";
        String message = "Bonjour,\n\n"
                + "Cliquez sur le lien suivant pour réinitialiser votre mot de passe :\n"
                + resetLink + "\n"
                + "Ce lien expirera dans " + (EXPIRY_TIME / 60000) + " minutes.\n\n"
                + "Cordialement,\nL'équipe Support";

        emailService.sendEmail(email, subject, message);
    }


    @Override
    @Transactional
    public void resetPassword(String email, String tokenValue, String newPassword) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("Utilisateur introuvable avec l'email : " + email);
        }

        Token token = tokenRepository.findByTokenAndTokenType(tokenValue, TokenType.RESET_PASSWORD)
                .orElseThrow(() -> new IllegalArgumentException("Token invalide !"));

        if (token.isExpired()) {
            throw new IllegalArgumentException("Le token a expiré !");
        }

        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);

        tokenRepository.delete(token);
    }

    @Override
    public List<User> findAllAdmins() {
        return userRepository.findByRole(UserRole.ADMIN);
    }



}
