package tn.sip.notificationservice.repositories;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.sip.notificationservice.entities.Notification;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // Recherche par userId et tri par date décroissante
    List<Notification> findByUserIdOrderByCreatedDateDesc(Long userId);

    @Query("SELECT n FROM Notification n WHERE n.userId = :userId " +
            "AND (:searchTerm IS NULL OR LOWER(n.message) LIKE LOWER(CONCAT('%', :searchTerm, '%'))) " +
            "AND (:seen IS NULL OR n.seen = :seen)")
    Page<Notification> findAllNotificationsByUser(
            @Param("searchTerm") String searchTerm,
            @Param("seen") Boolean seen,
            Pageable pageable,  // Pas besoin de @Param ici pour Pageable
            @Param("userId") Long userId
    );

    @Modifying
    @Query("UPDATE Notification n SET n.seen = true WHERE n.userId = :userId")
    void markAllNotificationsAsSeen(@Param("userId") Long userId);

    @Modifying
    @Query("UPDATE Notification n SET n.seen = true WHERE n.id = :notificationId AND n.userId = :userId")
    void markNotificationAsSeen(@Param("notificationId") Long notificationId, @Param("userId") Long userId);

    Page<Notification> findByUserIdAndSeen(Long userId, Boolean seen, Pageable pageable);

    Page<Notification> findByUserIdAndMessageContainingIgnoreCaseAndSeen(Long userId, String message, Boolean seen, Pageable pageable);

    @Modifying
    @Query("UPDATE Notification n SET n.seen = true WHERE n.userId = :userId")
    void markAllNotificationsAsSeenByUserId(Long userId);

    void deleteAllByUserId(Long userId);

    Page<Notification> findByUserId(Long userId, Pageable pageable);

    Page<Notification> findByUserIdAndMessageContainingIgnoreCase(Long userId, String searchTerm, Pageable pageable);
}
