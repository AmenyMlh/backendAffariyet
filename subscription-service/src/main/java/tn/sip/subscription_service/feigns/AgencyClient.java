package tn.sip.subscription_service.feigns;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import tn.sip.subscription_service.dto.AgencyDTO;
import tn.sip.subscription_service.dto.UserDTO;

import java.util.List;

@FeignClient(name = "user-service", url = "http://localhost:8090")
public interface AgencyClient {
    @GetMapping("/api/users/get/agency/{id}")
    AgencyDTO getAgencyById(@PathVariable("id") Long id);
    @GetMapping("/api/users/{id}")
    UserDTO getUserById(@PathVariable Long id);

    @GetMapping("/api/users/admins")
    List<UserDTO> getAllAdmins();

    @PutMapping("/api/users/{agencyId}/approve-payment")
    void approvePayment(@PathVariable("agencyId") Long agencyId, @RequestParam("approved") boolean approved);


}
