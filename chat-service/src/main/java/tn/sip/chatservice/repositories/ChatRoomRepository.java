package tn.sip.chatservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import tn.sip.chatservice.entities.ChatRoom;

import java.util.List;
import java.util.Optional;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
    Optional<ChatRoom> findByClientIdAndAgencyIdAndPropertyId(Long clientId, Long agencyId, Long propertyId);
    Optional<ChatRoom> findFirstByPropertyId(Long propertyId);
    List<ChatRoom> findByAgencyId(Long agencyId);
    List<ChatRoom> findByClientId(Long clientId);
    @Query("SELECT c FROM ChatRoom c WHERE c.propertyId = :propertyId AND " +
            "((c.clientId = :id1 AND c.agencyId = :id2) OR (c.clientId = :id2 AND c.agencyId = :id1))")
    Optional<ChatRoom> findByParticipantsAndProperty(@Param("propertyId") Long propertyId,
                                                     @Param("id1") Long id1,
                                                     @Param("id2") Long id2);

    Optional<ChatRoom> findByPropertyIdAndAgencyId(Long propertyId, Long agencyId);







}


