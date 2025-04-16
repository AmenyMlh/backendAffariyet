package tn.sip.user_service.servicesImpl;

import java.beans.Transient;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import tn.sip.user_service.dto.AgencyDTO;
import tn.sip.user_service.dto.DocumentsDTO;
import tn.sip.user_service.entities.Agency;
import tn.sip.user_service.entities.User;
import tn.sip.user_service.feigns.SubscriptionClient;
import tn.sip.user_service.mappers.AgencyMapper;
import tn.sip.user_service.repositories.AgencyRepository;
import tn.sip.user_service.services.AgencyService;
import tn.sip.user_service.services.EmailService;

@Service
public class AgencyServiceImpl implements AgencyService {
	@Autowired
	private AgencyRepository agencyRepository;
	@Autowired
	private EmailService emailService;
	@Autowired
	private SubscriptionClient subscriptionClient;
    @Autowired
    private AgencyMapper agencyMapper;

	@Override
		public DocumentsDTO getAgencyDocumentsByUserId(DocumentsDTO documents, User user) {
		  Agency agency = agencyRepository.findByUser(user);

		   if(agency.getPatenteFile()== null || agency.getRneFile() == null) {
			   documents.setMessage("Veuillez Completez les documents");
			   return documents;
		   }
		   else {
			   documents.setPatenteFile(agency.getPatenteFile());
			   documents.setRneFile(agency.getRneFile());
			   return documents;
		   }
	 }

	 @Override
	    @Transient
	    public Agency saveAgency(Agency agency) {
	        return agencyRepository.save(agency);
	    }

	    @Override
	    public Agency getAgencyByUser(User userId) {
	        return agencyRepository.findByUser(userId);
	    }

	@Override
	public AgencyDTO getAgencyById(Long id) {
		 Agency agency = agencyRepository.findById(id).orElse(null);
		 AgencyDTO dto = agencyMapper.toAgencyDTO(agency);
		 dto.setUserId(agency.getUser().getId());
		return dto;
	}

	@Override
	public Agency getAgencyByUserAndSubscriptionId(User userId, Long subscriptionId) {
		return agencyRepository.findAgencyByUserAndSubscriptionId(userId,subscriptionId);
	}

	    @Override
	    public Agency updateAgency(User user, Agency updatedAgency) {
	        Agency existingAgency = agencyRepository.findByUser(user);

	        if (existingAgency == null) {
	            throw new RuntimeException("Agence non trouvée pour cet utilisateur");
	        }

	        existingAgency.setAgencyName(updatedAgency.getAgencyName());
	        existingAgency.setRneFile(updatedAgency.getRneFile());
	        existingAgency.setPatenteFile(updatedAgency.getPatenteFile());

	        return agencyRepository.save(existingAgency);
	    }
	@Override
	@Scheduled(cron = "0 0 8 * * ?")
	public void checkSubscriptionExpirationAndSendReminder() {
		Iterable<Agency> agencies = agencyRepository.findAll();

		for (Agency agency : agencies) {
			if (agency.getEndDate() != null) {
				LocalDate endDate = agency.getEndDate();
				LocalDate currentDate = LocalDate.now();
				long daysUntilExpiration = ChronoUnit.DAYS.between(currentDate, endDate);

				if (daysUntilExpiration <= 15 || daysUntilExpiration == 7 || daysUntilExpiration == 3) {
					String emailSubject = "Rappel : Votre abonnement expire bientôt";
					String emailBody = "Bonjour " + agency.getAgencyName() + ",\n\nVotre abonnement expire le " + endDate.toString() +
							". Veuillez renouveler votre abonnement pour éviter toute interruption de service.\n\nCordialement,\nL'équipe AFFARIYET";

					emailService.sendEmail(
							agency.getUser().getEmail(),
							emailSubject,
							emailBody
					);
				}
			} else {
				System.out.println("L'agence " + agency.getAgencyName() + " n'a pas de date d'expiration définie.");
			}
		}
	}



}
