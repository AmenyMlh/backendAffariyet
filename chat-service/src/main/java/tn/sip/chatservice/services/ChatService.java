package tn.sip.chatservice.services;

import org.springframework.web.bind.annotation.PathVariable;
import tn.sip.chatservice.entities.ChatRoom;
import tn.sip.chatservice.entities.Message;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface ChatService {
    ChatRoom createOrGetRoom(Long propertyId, Long clientId, Long agencyId);

    Message sendMessage(Long roomId, Long senderId, String senderType, String content);

    Optional<ChatRoom> getChatRoomById(@PathVariable Long id);

    List<Message> getMessages(Long roomId);

    Long getAgencyIdByProperty(Long propertyId);

    Long getAgencyIdByPropertyId(Long propertyId);

    List<ChatRoom> getRoomsByAgency(Long agencyId);

    List<ChatRoom> getChatRoomsForClient(Long clientId);

    Optional<ChatRoom> findByPropertyIdAndAgencyId(Long propertyId, Long agencyId);

    Map<String, String> getUserNames(Long clientId, Long agencyId);
}
