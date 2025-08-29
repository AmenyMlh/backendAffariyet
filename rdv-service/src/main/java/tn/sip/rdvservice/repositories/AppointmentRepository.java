package tn.sip.rdvservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.sip.rdvservice.entities.Appointment;
import tn.sip.rdvservice.enums.AppointmentStatus;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface AppointmentRepository extends JpaRepository<Appointment, Long> {
    List<Appointment> findByAgencyId(Long agencyId);
    List<Appointment> findByClientId(Long clientId);

    Optional<Appointment> findByAgencyIdAndAppointmentDate(Long agencyId, LocalDateTime appointmentDate);

    boolean existsByClientIdAndPropertyIdAndStatusIn(Long clientId, Long propertyId, List<AppointmentStatus> statuses);

    List<Appointment> findByClientIdAndStatusIn(Long clientId, List<AppointmentStatus> statuses);

}
