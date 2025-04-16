package tn.sip.subscription_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Bank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false, unique = true, length = 24)
    private String rib;

    @Column(nullable = false, unique = true, length = 34)
    private String iban;

    @Column(name = "rib_url")
    private String ribUrl;

}

