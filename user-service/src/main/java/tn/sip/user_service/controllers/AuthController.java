package tn.sip.user_service.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tn.sip.user_service.config.JwtTokenProvider;
import tn.sip.user_service.dto.AgencyDTO;
import tn.sip.user_service.dto.DocumentsDTO;
import tn.sip.user_service.dto.LoginResponse;
import tn.sip.user_service.dto.UserDTO;
import tn.sip.user_service.entities.Agency;
import tn.sip.user_service.entities.User;
import tn.sip.user_service.enums.UserRole;
import tn.sip.user_service.exceptions.ErrorResponse;
import tn.sip.user_service.mappers.UserMapper;
import tn.sip.user_service.services.AgencyService;
import tn.sip.user_service.services.UserService;
import tn.sip.user_service.servicesImpl.UserDetailsServiceImpl;

@RestController
@RequestMapping("/auth")
//@CrossOrigin(origins = "http://localhost:4200")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsServiceImpl userDetailsService;
    private final UserService userService;
    @Autowired
    private AgencyService agencyService;
    @Autowired
    private UserMapper userMapper;
    public AuthController(AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider, UserDetailsServiceImpl userDetailsService, UserService userService) {
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
		this.userService = userService;

    }
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestParam String email, @RequestParam String password) {
        try {
            authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(email, password));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Email ou mot de passe incorrect.") {
                    });
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Erreur lors de l'authentification : " + e.getMessage()));
        }

        Optional<UserDTO> optionalUserDTO = userService.getUserByEmail(email);
        if (!optionalUserDTO.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ErrorResponse("Utilisateur non trouvé avec l'email : " + email));
        }

        UserDTO userDto = optionalUserDTO.get();
        User user = userMapper.toUser(userDto);
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        LoginResponse loginResponse = new LoginResponse();
        loginResponse.setId(user.getId());
        loginResponse.setEmail(email);
        loginResponse.setFirstName(user.getFirstName());
        loginResponse.setLastName(user.getLastName());
        loginResponse.setRole(user.getRole());
        loginResponse.setJwt(jwtTokenProvider.generateToken(userDto));
        loginResponse.setApproved(user.isApproved());

        if (UserRole.AGENCY.equals(userDto.getRole())) {
            DocumentsDTO missingDocs = userService.getUserDocuments(email);
            loginResponse.setMissingDocuments(missingDocs);
        }

        return ResponseEntity.ok(loginResponse);
    }



    @PostMapping("/refresh-token")
    public ResponseEntity<Map<String, String>> refreshToken(@RequestParam String refreshToken) {
        Map<String, String> response = new HashMap<>();

        try {
            String email = jwtTokenProvider.extractEmailFromRefreshToken(refreshToken);
            Optional<UserDTO> optionalUserDTO = userService.getUserByEmail(email);

            if (optionalUserDTO.isPresent()) {
                UserDTO userDTO = optionalUserDTO.get();

                User user = userMapper.toUser(optionalUserDTO.get());

                if (jwtTokenProvider.isRefreshTokenValid(refreshToken, user)) {
                    String newToken = jwtTokenProvider.generateToken(userDTO);
                    response.put("jwt", newToken);
                    response.put("refreshToken", refreshToken);
                    return ResponseEntity.ok(response);
                }
            }
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Map<String, Object>> forgotPassword(@RequestParam String email) {
        Map<String, Object> response = new HashMap<>();

        Optional<UserDTO> optionalUserDTO = userService.getUserByEmail(email);
        if (optionalUserDTO.isEmpty()) {
            response.put("success", false);
            response.put("message", "Utilisateur introuvable");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        userService.generateAndSendResetToken(email);

        response.put("success", true);
        response.put("message", "Le lien de réinitialisation du mot de passe a été envoyé à votre adresse e-mail.");

        return ResponseEntity.ok(response);
    }


    @PostMapping("/reset-password")
    public ResponseEntity<Map<String, Object>> resetPassword(
            @RequestParam String email,
            @RequestParam String resetToken,
            @RequestParam String newPassword) {

        Map<String, Object> response = new HashMap<>();

        try {
            userService.resetPassword(email, resetToken, newPassword);

            response.put("success", true);
            response.put("message", "Le mot de passe a été réinitialisé avec succès.");

            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        } catch (UsernameNotFoundException e) {
            response.put("success", false);
            response.put("message", e.getMessage());

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
    }



}
