package tn.sip.user_service.controllers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import org.springframework.web.multipart.MultipartFile;
import tn.sip.user_service.config.JwtTokenProvider;
import tn.sip.user_service.dto.*;
import tn.sip.user_service.entities.Agency;
import tn.sip.user_service.entities.Subscription;
import tn.sip.user_service.entities.User;
import tn.sip.user_service.enums.UserRole;
import tn.sip.user_service.mappers.UserMapper;
import tn.sip.user_service.services.AgencyService;
import tn.sip.user_service.services.UserService;
import tn.sip.user_service.servicesImpl.UserDetailsServiceImpl;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

	    private final UserService userService;
	 private final AgencyService agencyService;
	 private final BCryptPasswordEncoder bCryptPasswordEncoder;
	 private final JwtTokenProvider jwtTokenProvider;
	@Value("${file.uploads.photos-output-path}")
	private String fileUploadPath;


	 @GetMapping
	    public ResponseEntity<List<User>> getAllUsers() {
	        List<User> users = userService.getAllUsers();
	        return new ResponseEntity<>(users, HttpStatus.OK);
	    }

	 @GetMapping("/{id}")
	    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
	        Optional<UserDTO> userResponse = userService.getUserById(id);
	        return userResponse.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	    }
	 @GetMapping("/email/{email}")
	 public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
	     Optional<UserDTO> userDTO = userService.getUserByEmail(email);
	     return userDTO.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
	 }


	    @PostMapping("/send-code")
	    public ResponseEntity<String> sendVerificationCode(@RequestParam String email) {
	        userService.sendVerificationCode(email);
	        return ResponseEntity.ok("Code de vérification envoyé à " + email);
	    }

	@PostMapping("/verify-code")
	public ResponseEntity<Map<String, Object>> verifyUserCode(@RequestBody VerificationRequest request) {
		Map<String, Object> response = new HashMap<>();

		boolean isVerified = userService.verifyCode(request.getEmail(), request.getCode());
		if (!isVerified) {
			response.put("success", false);
			response.put("message", "Code invalide ou expiré");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}

		try {
			Optional<UserDTO> userOptional = userService.getUserByEmail(request.getEmail());
			if (userOptional.isEmpty()) {
				response.put("success", false);
				response.put("message", "Utilisateur non trouvé");
				return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
			}

			UserDTO userDTO = userOptional.get();
			//User user = userMapper.toUser(userOptional.get());
			String token = jwtTokenProvider.generateToken(userDTO);

			response.put("success", true);
			response.put("message", "Code vérifié avec succès");
			response.put("token", token);

			return ResponseEntity.ok(response);
		} catch (Exception e) {
			response.put("success", false);
			response.put("message", "Erreur lors de la vérification du code");
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
		}
	}



	@PutMapping("/change-password")
	public ResponseEntity<Map<String, Object>> changePassword(
			@RequestBody ChangePasswordRequest request,
			Authentication authentication) {

		Map<String, Object> response = new HashMap<>();

		if (authentication == null || !(authentication.getPrincipal() instanceof UserDetails)) {
			response.put("success", false);
			response.put("message", "Utilisateur non authentifié");
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
		}

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		Optional<UserDTO> optionalUser = userService.getUserByEmail(userDetails.getUsername());

		if (optionalUser.isEmpty()) {
			response.put("success", false);
			response.put("message", "Utilisateur introuvable");
			return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
		}

		boolean passwordChanged = userService.changePassword(optionalUser.get().getId(), request);
		if (passwordChanged) {
			response.put("success", true);
			response.put("message", "Mot de passe changé avec succès");
			return ResponseEntity.ok(response);
		} else {
			response.put("success", false);
			response.put("message", "Mot de passe actuel incorrect");
			return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
		}
	}


	@PostMapping("/add")
	public ResponseEntity<?> createUser(@RequestBody RegisterRequest registerRequest) {
		try {
			User newUser = new User();
			newUser.setFirstName(registerRequest.getFirstName());
			newUser.setLastName(registerRequest.getLastName());
			newUser.setEmail(registerRequest.getEmail());
			newUser.setPassword(bCryptPasswordEncoder.encode(registerRequest.getPassword()));
			newUser.setPhoneNumber(registerRequest.getPhoneNumber());
			newUser.setRole(registerRequest.getRole());
			newUser.setApproved(false);

			newUser = userService.createUser(newUser, registerRequest);

			userService.sendVerificationCode(newUser.getEmail());

			return ResponseEntity.status(HttpStatus.CREATED).body(newUser);
		} catch (Exception e) {
			String errorMessage = "Erreur lors de la création de l'utilisateur : " + e.getMessage();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorMessage);
		}
	}

	@PutMapping(value = "/update/{userId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<User> updateUser(
			@PathVariable Long userId,
			@ModelAttribute UserUpdateRequest request) {
		User updatedUser = userService.updateUser(userId, request);
		return ResponseEntity.ok(updatedUser);
	}

	@PostMapping(value = "/upload-profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<Map<String, String>> uploadProfilePicture(
			@RequestParam("file") MultipartFile file,
			@RequestParam("userId") Long userId
	) throws IOException {
		String profilePictureUrl = userService.uploadProfilePicture(file, userId);
		Map<String, String> response = new HashMap<>();
		response.put("message", "Profile picture uploaded successfully");
		response.put("profilePictureUrl", profilePictureUrl);
		return ResponseEntity.ok(response);
	}

	@GetMapping(path = "/profilePicture/{fileName}")
	public ResponseEntity<byte[]> getProfilePicture(@PathVariable("fileName") String fileName) throws IOException {
		Path imagePath = Paths.get(fileUploadPath).resolve("profilePictures").resolve(fileName).normalize();

		if (!Files.exists(imagePath) || !Files.isReadable(imagePath)) {
			return ResponseEntity.notFound().build();
		}

		byte[] fileContent = Files.readAllBytes(imagePath);
		String mimeType = Files.probeContentType(imagePath);

		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.CONTENT_TYPE, mimeType);

		return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
	}

	@GetMapping(path = "/files/cin/{fileName}")
	public ResponseEntity<byte[]> getCinFile(@PathVariable("fileName") String fileName) throws IOException {
		return getFileResponse("cin", fileName);
	}

	@GetMapping(path = "/files/rne/{fileName}")
	public ResponseEntity<byte[]> getRneFile(@PathVariable("fileName") String fileName) throws IOException {
		return getFileResponse("rne", fileName);
	}

	@GetMapping(path = "/files/patente/{fileName}")
	public ResponseEntity<byte[]> getPatenteFile(@PathVariable("fileName") String fileName) throws IOException {
		return getFileResponse("patente", fileName);
	}


	private ResponseEntity<byte[]> getFileResponse(String folder, String fileName) throws IOException {
		Path filePath = Paths.get(fileUploadPath).resolve(folder).resolve(fileName).normalize();

		if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
			return ResponseEntity.notFound().build();
		}

		byte[] fileContent = Files.readAllBytes(filePath);
		String mimeType = Files.probeContentType(filePath);

		HttpHeaders headers = new HttpHeaders();
		headers.set(HttpHeaders.CONTENT_TYPE, mimeType);

		return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
	}



	@GetMapping("/not-approved")
	    public ResponseEntity<List<UserDTO>> getNotApprovedUsersWithDocuments() {
	        List<UserDTO> users = userService.getNotApprovedUsersWithDocuments();
	        return new ResponseEntity<>(users, HttpStatus.OK);
	    }
	    @GetMapping("/verify-documents")
	    public ResponseEntity<DocumentsDTO> verifyDocuments(Authentication authentication) {
			System.out.println(authentication);
			DocumentsDTO missingDocs = userService.getUserDocuments(authentication.getName());
			return  ResponseEntity.ok(missingDocs);
	    }

	    @PutMapping("/approve/{id}")
	    public ResponseEntity<Void> approveUser(@PathVariable Long id) {
	        try {
	            userService.approveUser(id);
	            return ResponseEntity.noContent().build();
	        } catch (IllegalArgumentException e) {
	            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
	        }
	    }

	    @GetMapping("/agency/{userId}")
	    public Agency getAgencyByUser(@PathVariable("userId") Long userId) {
	        User user = new User();
	        user.setId(userId);

	        return agencyService.getAgencyByUser(user);
	    }
	@GetMapping("/get/agency/{id}")
	public Agency getAgencyById(@PathVariable("id") Long id) {
		return agencyService.getAgencyById(id);
	}

	@GetMapping("/agency/{userId}/{subscriptionId}")
	public Agency getAgencyBySubscriptionId(@PathVariable("userId") Long userId,@PathVariable("subscriptionId") Long subscriptionId) {
		User user = new User();
		user.setId(userId);
		return agencyService.getAgencyByUserAndSubscriptionId(user,subscriptionId);
	}
	@GetMapping("/testReminder")
	public String testReminder() {
		agencyService.checkSubscriptionExpirationAndSendReminder();
		return "Reminder check triggered!";
	}
	@GetMapping("/admins")
	public List<UserDTO> getAllAdmins() {
		List<User> user = userService.findAllAdmins();
		List<UserDTO> dtos = UserMapper.INSTANCE.toUsersDTO(user);
		return dtos;
	}

	@PutMapping("/{agencyId}/approve-payment")
	public ResponseEntity<Void> approvePayment(@PathVariable Long agencyId, @RequestParam boolean approved) {
		boolean updated = agencyService.updatePaymentApproval(agencyId, approved);
		if (!updated) {
			return ResponseEntity.notFound().build();
		}
		return ResponseEntity.ok().build();
	}

}
