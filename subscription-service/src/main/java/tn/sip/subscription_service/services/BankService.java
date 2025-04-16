package tn.sip.subscription_service.services;

import tn.sip.subscription_service.entities.Bank;

import java.util.List;
import java.util.Optional;

public interface BankService {
    Bank createBank(Bank bank);
    List<Bank> getAllBanks();

    Bank updateBank(Long id, Bank bank);

    Optional<Bank> getBankById(Long id);
    void deleteBank(Long id);
}
