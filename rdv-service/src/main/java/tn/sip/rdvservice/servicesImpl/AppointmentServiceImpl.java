package tn.sip.rdvservice.servicesImpl;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.sip.rdvservice.dtos.AppointmentRequestDto;
import tn.sip.rdvservice.dtos.NotificationRequest;
import tn.sip.rdvservice.dtos.UserDTO;
import tn.sip.rdvservice.entities.Appointment;
import tn.sip.rdvservice.enums.AppointmentStatus;
import tn.sip.rdvservice.feigns.NotificationClient;
import tn.sip.rdvservice.feigns.UserClient;
import tn.sip.rdvservice.repositories.AppointmentRepository;
import tn.sip.rdvservice.services.AppointmentService;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AppointmentServiceImpl implements AppointmentService {
    private final AppointmentRepository appointmentRepository;
    private final UserClient userClient;
    private final NotificationClient notificationClient;

    @Override
    public Appointment createAppointment(AppointmentRequestDto dto) {
        LocalDateTime dateTime = dto.getAppointmentDate();

        if (!isAgentAvailable(dto.getAgencyId(), dateTime)) {
            throw new IllegalStateException("Agence non disponible à cette date.");
        }
        Appointment appointment = Appointment.builder()
                .propertyId(dto.getPropertyId())
                .clientId(dto.getClientId())
                .agencyId(dto.getAgencyId())
                .appointmentDate(dto.getAppointmentDate())
                .notes(dto.getNotes())
                .status(AppointmentStatus.PENDING)
                .build();

        Appointment saved = appointmentRepository.save(appointment);

        NotificationRequest notification = new NotificationRequest();
        notification.setUserId(dto.getAgencyId());
        notification.setMessage("Vous avez une nouvelle demande de rendez-vous.");
        notification.setUrl("/appointments/"+saved.getAgencyId());

        notificationClient.sendNotification(notification);

        return saved;
    }


    @Override
    public Appointment getAppointmentById(Long id) {
        return appointmentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Appointment not found"));
    }

    @Override
    public List<Appointment> getAppointmentsForAgency(Long agencyId) {
        return appointmentRepository.findByAgencyId(agencyId);
    }

    @Override
    public List<Appointment> getAppointmentsForClient(Long clientId) {
        return appointmentRepository.findByClientIdAndStatusIn(clientId, Arrays.asList(AppointmentStatus.CONFIRMED, AppointmentStatus.CANCELLED, AppointmentStatus.PENDING));
    }

    @Override
    public Appointment confirmAppointment(Long id) {
        Appointment appointment = getAppointmentById(id);
        appointment.setStatus(AppointmentStatus.CONFIRMED);
        Appointment confirmed = appointmentRepository.save(appointment);

        NotificationRequest notification = new NotificationRequest();
        notification.setUserId(appointment.getClientId());
        notification.setMessage("Votre rendez-vous a été confirmé.");
        notification.setUrl("/appointments/"+confirmed.getClientId());

        notificationClient.sendNotification(notification);
        return confirmed;
    }

    @Override
    public void cancelAppointment(Long id, Appointment updated) {
        Appointment appointment = getAppointmentById(id);
        appointment.setStatus(AppointmentStatus.CANCELLED);
        appointment.setNotes(updated.getNotes());
        appointmentRepository.save(appointment);

        // ✅ Notify client
        NotificationRequest notification = new NotificationRequest();
        notification.setUserId(appointment.getClientId());
        notification.setMessage("Votre rendez-vous a été annulé.");
        notification.setUrl("/appointments/"+updated.getId());

        notificationClient.sendNotification(notification);
    }



    @Override
    public boolean isAgentAvailable(Long agencyId, LocalDateTime appointmentDateTime) {
        return appointmentRepository
                .findByAgencyIdAndAppointmentDate(agencyId, appointmentDateTime)
                .isEmpty();
    }

    @Override
    public boolean hasAppointmentForPropertyAndClient(Long clientId, Long propertyId) {
        return appointmentRepository.existsByClientIdAndPropertyIdAndStatusIn(clientId, propertyId,Arrays.asList(AppointmentStatus.CONFIRMED, AppointmentStatus.PENDING));
    }

    @Override
    public UserDTO getUserById(Long clientId) {
        UserDTO client = userClient.getUserById(clientId);
        return client;
    }

    @Override
    public Appointment confirmVisit(Long appointmentId, boolean byClient) {
        Appointment appointment = getAppointmentById(appointmentId);

        // Marquer la confirmation selon le rôle
        if (byClient) {
            appointment.setClientConfirmed(true);
        } else {
            appointment.setAgencyConfirmed(true);
        }

        // Si les deux ont confirmé → status = VISITED
        if (appointment.isClientConfirmed() && appointment.isAgencyConfirmed()) {
            appointment.setStatus(AppointmentStatus.VISITED);

            // ✅ Notifier les deux parties
            NotificationRequest notifyAgency = new NotificationRequest();
            notifyAgency.setUserId(appointment.getAgencyId());
            notifyAgency.setMessage("La visite a été confirmée par les deux parties.");
            notifyAgency.setUrl("/appointments/" + appointmentId);
            notificationClient.sendNotification(notifyAgency);

            NotificationRequest notifyClient = new NotificationRequest();
            notifyClient.setUserId(appointment.getClientId());
            notifyClient.setMessage("La visite a été confirmée par les deux parties.");
            notifyClient.setUrl("/appointments/" + appointmentId);
            notificationClient.sendNotification(notifyClient);

        } else {
            // ✅ Une seule partie a confirmé → notifier l'autre
            Long otherUserId = byClient ? appointment.getAgencyId() : appointment.getClientId();
            String confirmer = byClient ? "le client" : "l'agence";

            NotificationRequest notifyOtherParty = new NotificationRequest();
            notifyOtherParty.setUserId(otherUserId);
            notifyOtherParty.setMessage("La visite a été confirmée par " + confirmer + ". Veuillez la confirmer également.");
            notifyOtherParty.setUrl("/appointments/" + appointmentId); // Lien correct
            notificationClient.sendNotification(notifyOtherParty);
        }

        return appointmentRepository.save(appointment);
    }







}
