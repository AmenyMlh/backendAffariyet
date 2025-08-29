package tn.sip.notificationservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.sip.notificationservice.entities.Notification;
import tn.sip.notificationservice.responses.NotificationResponse;
import tn.sip.notificationservice.services.NotificationService;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:4200")
public class NotificationController {
    private final NotificationService notificationService;


    @PostMapping
    public ResponseEntity<Void> sendNotification(@RequestBody Notification notification) {
        notificationService.sendNotification(notification);
        return ResponseEntity.ok().build();
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<NotificationResponse>> getNotificationsByUser(
            @PathVariable Long userId,
            @RequestParam(required = false) String searchTerm,
            @RequestParam(required = false) Boolean seen,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Page<NotificationResponse> notifications = notificationService.getNotificationsByUser(userId, searchTerm, seen, page, size);
        return ResponseEntity.ok(notifications);
    }


    @PutMapping("/user/{userId}/mark-all-seen")
    public ResponseEntity<Void> markAllAsSeen(@PathVariable Long userId) {
        notificationService.markAllNotificationsAsSeen(userId);
        return ResponseEntity.ok().build();
    }


    @PutMapping("/{notificationId}/user/{userId}/mark-seen")
    public ResponseEntity<Void> markNotificationAsSeen(
            @PathVariable Long notificationId,
            @PathVariable Long userId) {
        notificationService.markNotificationAsSeen(notificationId, userId);
        return ResponseEntity.ok().build();
    }


    @DeleteMapping("/user/{userId}")
    public ResponseEntity<Void> deleteNotificationsByUser(@PathVariable Long userId) {
        notificationService.deleteNotificationsByUser(userId);
        return ResponseEntity.ok().build();
    }
}
