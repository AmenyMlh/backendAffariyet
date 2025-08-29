package tn.sip.reviewservice.feigns;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import tn.sip.reviewservice.dtos.NotificationRequest;

@FeignClient(name = "notification-service", url = "${notification.service.url}")
public interface NotificationClient {

    @PostMapping("/api/notifications")
    void sendNotification(@RequestBody tn.sip.reviewservice.dtos.NotificationRequest request);
}
