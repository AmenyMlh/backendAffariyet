package tn.sip.subscription_service.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tn.sip.subscription_service.dto.PaymentDTO;
import tn.sip.subscription_service.entities.Payment;
import tn.sip.subscription_service.services.PaymentService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {
    private final PaymentService paymentService;
    @Value("${file.uploads.photos-output-path}")
    private String fileUploadPath;

    @PostMapping
    public ResponseEntity<Payment> createPayment(
            @RequestPart("payment") Payment payment,
            @RequestPart(value = "attachment", required = false) MultipartFile attachment
    ) throws IOException {
        Payment saved = paymentService.createPayment(payment, attachment);
        return ResponseEntity.ok(saved);
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

    private ResponseEntity<byte[]> getFileResponse(String folder, String fileName) throws IOException {
        Path filePath = Paths.get(fileUploadPath).resolve(folder).resolve(fileName).normalize();

        if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
            return ResponseEntity.notFound().build();
        }

        byte[] fileContent = Files.readAllBytes(filePath);
        String mimeType = Files.probeContentType(filePath);
        if (mimeType == null) {
            mimeType = "application/octet-stream"; // fallback si non détecté
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(mimeType));
        // Pour afficher directement dans le navigateur sans forcer le téléchargement :
        headers.setContentDisposition(ContentDisposition.inline().filename(fileName).build());

        return new ResponseEntity<>(fileContent, headers, HttpStatus.OK);
    }

    @GetMapping(path = "/files/payments/{fileName:.+}") // .+ pour autoriser les extensions dans le path variable
    public ResponseEntity<byte[]> getAttachmentFile(@PathVariable("fileName") String fileName) throws IOException {
        return getFileResponse("payments", fileName);
    }

}
