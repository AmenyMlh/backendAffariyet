package tn.sip.user_service.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tn.sip.user_service.dto.UserDTO;
import tn.sip.user_service.entities.User;
import tn.sip.user_service.enums.UserRole;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	    User findByEmail(String email);

		Optional<User> findById(Long id);

	    List<User> findByRole(UserRole role);

	    List<User> findByIsApprovedFalseAndRole(UserRole role);




}
