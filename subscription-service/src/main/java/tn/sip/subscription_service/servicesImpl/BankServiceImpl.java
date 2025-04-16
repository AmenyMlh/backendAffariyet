package tn.sip.subscription_service.servicesImpl;

import org.springframework.stereotype.Service;
import tn.sip.subscription_service.entities.Bank;
import tn.sip.subscription_service.repositories.BankRepository;
import tn.sip.subscription_service.services.BankService;

import java.util.List;
import java.util.Optional;

@Service
public class BankServiceImpl implements BankService {
    private final BankRepository bankRepository;

    public BankServiceImpl(BankRepository bankRepository) {
        this.bankRepository = bankRepository;
    }
    @Override
    public Bank createBank(Bank bank) {
        return bankRepository.save(bank);
    }

    @Override
    public List<Bank> getAllBanks() {
        return bankRepository.findAll();
    }
    @Override
    public Bank updateBank(Long id, Bank bank) {
        Optional<Bank> existingBank = bankRepository.findById(id);
        if (existingBank.isPresent()) {
            Bank updatedBank = existingBank.get();
            updatedBank.setBankName(bank.getBankName());
            updatedBank.setRib(bank.getRib());
            updatedBank.setIban(bank.getIban());
            updatedBank.setRibUrl(bank.getRibUrl());
            return bankRepository.save(updatedBank);
        } else {
            throw new RuntimeException("Bank not found with ID: " + id);
        }
    }
    @Override
    public Optional<Bank> getBankById(Long id) {
        return bankRepository.findById(id);
    }

    @Override
    public void deleteBank(Long id) {
        bankRepository.deleteById(id);
    }
}
