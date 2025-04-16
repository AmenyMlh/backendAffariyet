package tn.sip.subscription_service.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.sip.subscription_service.dto.PaymentDTO;
import tn.sip.subscription_service.entities.Payment;
import tn.sip.subscription_service.services.PaymentService;

import java.util.List;

@RestController
@RequestMapping("/api/payments")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {
    private final PaymentService paymentService;

    @PostMapping
    public ResponseEntity<Payment> createPayment(@RequestBody Payment payment) {
        Payment savedPayment = paymentService.createPayment(payment);
        return ResponseEntity.ok(savedPayment);
    }

    @GetMapping
    public ResponseEntity<List<Payment>> getAllPayments() {
        return ResponseEntity.ok(paymentService.getAllPayments());
    }

    @GetMapping("/agency/{agencyId}")
    public ResponseEntity<List<Payment>> getByAgency(@PathVariable Long agencyId) {
        return ResponseEntity.ok(paymentService.getPaymentsByAgency(agencyId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Payment> getById(@PathVariable Long id) {
        return paymentService.getPaymentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        paymentService.deletePayment(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/unapproved")
    public ResponseEntity<List<PaymentDTO>> getUnapprovedPayments() {
        return ResponseEntity.ok(paymentService.getUnapprovedPayments());
    }

    @PutMapping("/approve/{id}")
    public ResponseEntity<String> approvePayment(@PathVariable Long id) {
        paymentService.approvePayment(id);
        return ResponseEntity.ok("Paiement approuvé avec succès !");
    }
}
