package tn.sip.notificationservice.servicesImpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tn.sip.notificationservice.entities.Notification;
import tn.sip.notificationservice.feigns.UserClient;
import tn.sip.notificationservice.mappers.NotificationMapper;
import tn.sip.notificationservice.repositories.NotificationRepository;
import tn.sip.notificationservice.responses.NotificationResponse;
import tn.sip.notificationservice.services.NotificationService;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class NotificationServiceImpl implements NotificationService {
    private final SimpMessagingTemplate messagingTemplate;
    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final UserClient userClient;

    @Override
    public void sendNotification(Notification notification) {
        Notification saved = notificationRepository.save(notification);
        NotificationResponse notificationResponse = notificationMapper.toNotificationResponse(saved);

        messagingTemplate.convertAndSendToUser(
                String.valueOf(notificationResponse.getUserId()),
                "/notifications",
                notificationResponse
        );
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificationResponse> getNotificationsByUser(Long userId, String searchTerm, Boolean seen, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdDate").descending());
        Page<Notification> notifications;

        if (searchTerm == null || searchTerm.isEmpty()) {
            if (seen == null) {
                notifications = notificationRepository.findByUserId(userId, pageable);
            } else {
                notifications = notificationRepository.findByUserIdAndSeen(userId, seen, pageable);
            }
        } else {
            if (seen == null) {
                notifications = notificationRepository.findByUserIdAndMessageContainingIgnoreCase(userId, searchTerm, pageable);
            } else {
                notifications = notificationRepository.findByUserIdAndMessageContainingIgnoreCaseAndSeen(userId, searchTerm, seen, pageable);
            }
        }


        return notifications.map(notification -> {
            NotificationResponse response = notificationMapper.toNotificationResponse(notification);
            return response;
        });
    }

    @Override
    public void markAllNotificationsAsSeen(Long userId) {
        notificationRepository.markAllNotificationsAsSeenByUserId(userId);
    }

    @Override
    public void markNotificationAsSeen(Long notificationId, Long userId) {
        Optional<Notification> notifOpt = notificationRepository.findById(notificationId);
        if (notifOpt.isPresent() && notifOpt.get().getUserId().equals(userId)) {
            Notification notification = notifOpt.get();
            notification.setSeen(true);
            notificationRepository.save(notification);
        }
    }

    @Override
    public void deleteNotificationsByUser(Long userId) {
        notificationRepository.deleteAllByUserId(userId);
    }
}
