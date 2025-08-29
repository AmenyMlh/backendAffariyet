package tn.sip.reviewservice.feigns;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "property-service", url = "http://localhost:8070")
public interface PropertyClient {
    @GetMapping("/api/properties/{propertyId}/available")
    boolean isPropertyAvailable(@PathVariable("propertyId") Long propertyId);
}
