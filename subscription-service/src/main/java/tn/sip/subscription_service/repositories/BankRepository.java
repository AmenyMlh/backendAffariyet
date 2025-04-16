package tn.sip.subscription_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.sip.subscription_service.entities.Bank;

import java.util.Optional;

public interface BankRepository extends JpaRepository<Bank, Long> {
    Optional<Bank> findByBankName(String bankName);
}
