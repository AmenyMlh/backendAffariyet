package tn.sip.notificationservice.mappers;


import org.springframework.stereotype.Service;
import tn.sip.notificationservice.entities.Notification;
import tn.sip.notificationservice.responses.NotificationResponse;

@Service
public class NotificationMapper {


    public NotificationResponse toNotificationResponse(Notification notification) {

        return NotificationResponse.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .createdDate(notification.getCreatedDate())
                .seen(notification.isSeen())
                .userId((notification.getUserId()))
                .url(notification.getUrl())
                .build();
    }
}
