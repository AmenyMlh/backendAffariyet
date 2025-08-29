package tn.sip.rdvservice.services;

import tn.sip.rdvservice.dtos.AppointmentRequestDto;
import tn.sip.rdvservice.dtos.UserDTO;
import tn.sip.rdvservice.entities.Appointment;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public interface AppointmentService {

    Appointment createAppointment(AppointmentRequestDto dto);

    Appointment getAppointmentById(Long id);

    List<Appointment> getAppointmentsForAgency(Long agencyId);

    List<Appointment> getAppointmentsForClient(Long clientId);

    Appointment confirmAppointment(Long id);

    void cancelAppointment(Long id,Appointment updated);

    boolean isAgentAvailable(Long agencyId, LocalDateTime appointmentDateTime);

    boolean hasAppointmentForPropertyAndClient(Long clientId, Long propertyId);

    UserDTO getUserById(Long clientId);

    Appointment confirmVisit(Long appointmentId, boolean byClient);
}
