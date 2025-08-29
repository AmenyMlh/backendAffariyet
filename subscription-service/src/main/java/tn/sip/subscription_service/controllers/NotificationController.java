package tn.sip.subscription_service.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@CrossOrigin(origins = "http://localhost:4200")
public class NotificationController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    // This method will be triggered when an agency sends a request for approval
    @PostMapping("/sendRequest")
    public void sendRequestNotification() {
        // Send a message to the topic where the admin is listening
        messagingTemplate.convertAndSend("/topic/admin-notifications", "New agency request for approval!");
    }
}

