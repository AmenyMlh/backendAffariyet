package tn.sip.subscription_service.feigns;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import tn.sip.subscription_service.dto.AgencyDTO;
import tn.sip.subscription_service.dto.UserDTO;

@FeignClient(name = "user-service", url = "http://localhost:8090")
public interface AgencyClient {
    @GetMapping("/api/users/get/agency/{id}")
    AgencyDTO getAgencyById(@PathVariable("id") Long id);
    @GetMapping("/api/users/{id}")
    UserDTO getUserById(@PathVariable Long id);
}
