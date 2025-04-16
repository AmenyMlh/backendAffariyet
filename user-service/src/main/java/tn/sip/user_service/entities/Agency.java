package tn.sip.user_service.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Entity
@Data
public class Agency {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

	@Column(nullable = false,name="agency_name")
    private String agencyName;

	@Column(name = "agency_rne")
    private String rneFile;

    @Column(name = "agency_patente")
    private String patenteFile;

    @OneToOne
    private User user;

    @Column(nullable = false)
    private Long subscriptionId;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    public void setSubscriptionDates(Subscription subscription) {
        this.startDate = LocalDate.now();
        this.endDate = this.startDate.plusMonths(subscription.getDurationInMonths());
    }
}
