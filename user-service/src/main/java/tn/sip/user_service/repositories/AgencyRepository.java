package tn.sip.user_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import tn.sip.user_service.dto.AgencyDTO;
import tn.sip.user_service.entities.Agency;
import tn.sip.user_service.entities.User;

import java.util.List;


public interface AgencyRepository extends JpaRepository<Agency, Long> {


	Agency findByUser(User user);
	Agency getAgencyById(Long id);
	Agency findAgencyByUserAndSubscriptionId(User user,Long subscriptionId);
}
