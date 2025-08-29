package tn.sip.user_service.services;

import tn.sip.user_service.dto.AgencyDTO;
import tn.sip.user_service.dto.DocumentsDTO;
import tn.sip.user_service.entities.Agency;
import tn.sip.user_service.entities.User;

import java.util.List;

public interface AgencyService {

	DocumentsDTO getAgencyDocumentsByUserId(DocumentsDTO documents, User user);
	Agency saveAgency(Agency agency);
	Agency getAgencyByUser(User userId);
	Agency getAgencyById(Long id);
	Agency getAgencyByUserAndSubscriptionId(User userId, Long subscriptionId);
	void checkSubscriptionExpirationAndSendReminder();
	Agency updateAgency(User user, Agency updatedAgency);

	boolean updatePaymentApproval(Long agencyId, boolean approved);
}
