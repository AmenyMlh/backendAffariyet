package tn.sip.rdvservice.dtos;

import jakarta.validation.constraints.Future;
import lombok.Data;
import org.antlr.v4.runtime.misc.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class AppointmentRequestDto {
    @NotNull
    private Long propertyId;

    @NotNull
    private Long clientId;

    @NotNull
    private Long agencyId;

    @NotNull
    @Future(message = "Appointment date must be in the future")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime appointmentDate;

    private String notes;
}
