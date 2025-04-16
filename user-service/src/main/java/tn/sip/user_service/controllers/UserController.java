package tn.sip.user_service.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")

public class UserController {
	 @Autowired
	    private UserService userService;
	 @Autowired
	 private AgencyService agencyService;
	 @Autowired
	 private BCryptPasswordEncoder bCryptPasswordEncoder;
	 @Autowired
	 private NotificationController notificationController;
	 private final JwtTokenProvider jwtTokenProvider;
	 private final UserDetailsServiceImpl userDetailsService;
     private final UserMapper userMapper;

	public UserController(JwtTokenProvider jwtTokenProvider, UserDetailsServiceImpl userDetailsService,UserMapper userMapper) {
	        this.jwtTokenProvider = jwtTokenProvider;
	        this.userDetailsService = userDetailsService;
			this.userMapper = userMapper;
	    }
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
	public ResponseEntity<User> createUser(@RequestBody RegisterRequest registerRequest) {
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
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}





	@PutMapping("/{id}")
	    public ResponseEntity<User> updateUser(
	        @PathVariable Long id,
	        @RequestBody UserUpdateRequest request) {

	        User savedUser = userService.updateUserWithoutPass(id, request);
	        return ResponseEntity.ok(savedUser);
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
	public AgencyDTO getAgencyById(@PathVariable("id") Long id) {
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

}
