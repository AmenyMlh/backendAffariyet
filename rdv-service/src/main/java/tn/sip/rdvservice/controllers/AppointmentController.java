package tn.sip.rdvservice.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import tn.sip.rdvservice.dtos.AppointmentRequestDto;
import tn.sip.rdvservice.dtos.UserDTO;
import tn.sip.rdvservice.entities.Appointment;
import tn.sip.rdvservice.services.AppointmentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/appointments")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:4200")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public Appointment create(@Valid @RequestBody AppointmentRequestDto requestDto) {
        return appointmentService.createAppointment(requestDto);
    }


    @GetMapping("/{id}")
    public Appointment getById(@PathVariable Long id) {
        return appointmentService.getAppointmentById(id);
    }

    @GetMapping("/agency/{agencyId}")
    public List<Appointment> getByAgency(@PathVariable Long agencyId) {
        return appointmentService.getAppointmentsForAgency(agencyId);
    }

    @GetMapping("/client/{clientId}")
    public List<Appointment> getByClient(@PathVariable Long clientId) {
        return appointmentService.getAppointmentsForClient(clientId);
    }

    @PutMapping("/{id}/confirm")
    public Appointment confirm(@PathVariable Long id) {
        return appointmentService.confirmAppointment(id);
    }

    @PutMapping("/{id}/cancel")
    public void cancel(@PathVariable Long id,@RequestBody Appointment updated) {
        appointmentService.cancelAppointment(id, updated);
    }

    @GetMapping("/check-availability")
    public boolean checkAvailability(
            @RequestParam Long agencyId,
            @RequestParam String date,
            @RequestParam String time
    ) {
        LocalDateTime dateTime = LocalDateTime.parse(date + "T" + time);
        return appointmentService.isAgentAvailable(agencyId, dateTime);
    }

    @GetMapping("/agency/{agencyId}/unavailable-slots")
    public List<LocalDateTime> getUnavailableSlots(@PathVariable Long agencyId) {
        return appointmentService.getAppointmentsForAgency(agencyId).stream()
                .map(Appointment::getAppointmentDate)
                .toList();
    }
    @GetMapping("/exist")
    public boolean hasExistingAppointment(@RequestParam Long clientId, @RequestParam Long propertyId) {
        return appointmentService.hasAppointmentForPropertyAndClient(clientId, propertyId);
    }

    @GetMapping("/user/{clientId}")
    public UserDTO getUserById(
            @PathVariable Long clientId) {

        return appointmentService.getUserById(clientId);
    }

    @PutMapping("/{id}/confirm-visit")
    public Appointment confirmVisit(
            @PathVariable("id") Long appointmentId,
            @RequestBody Map<String, Boolean> payload) {

        if (!payload.containsKey("byClient")) {
            throw new IllegalArgumentException("Le champ 'byClient' est requis.");
        }

        boolean byClient = payload.get("byClient");
        return appointmentService.confirmVisit(appointmentId, byClient);
    }









}
