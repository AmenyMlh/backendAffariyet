package tn.sip.notificationservice.services;

import org.springframework.data.domain.Page;
import tn.sip.notificationservice.entities.Notification;
import tn.sip.notificationservice.responses.NotificationResponse;

public interface NotificationService {
    void sendNotification(Notification notification);

    Page<NotificationResponse> getNotificationsByUser(Long userId, String searchTerm, Boolean seen, int page, int size);

    void markAllNotificationsAsSeen(Long userId);

    void markNotificationAsSeen(Long notificationId, Long userId);

    void deleteNotificationsByUser(Long userId);
}
