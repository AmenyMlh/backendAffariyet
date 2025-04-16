package tn.sip.subscription_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long subscriptionId;

    private Long agencyId;

    private LocalDate startDate;

    private LocalDate endDate;

    private Double amount;

    private String attachment;

    @Column(name = "is_approved")
    private boolean isApproved = false;

    public void calculateEndDate(Integer durationInMonths) {
        if (startDate != null && durationInMonths != null) {
            this.endDate = this.startDate.plusMonths(durationInMonths);
        }
    }
}
