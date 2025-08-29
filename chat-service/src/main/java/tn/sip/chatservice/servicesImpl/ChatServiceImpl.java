package tn.sip.chatservice.servicesImpl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tn.sip.chatservice.dto.NotificationRequest;
import tn.sip.chatservice.dto.UserDTO;
import tn.sip.chatservice.entities.ChatRoom;
import tn.sip.chatservice.entities.Message;
import tn.sip.chatservice.feigns.NotificationClient;
import tn.sip.chatservice.feigns.PropertyClient;
import tn.sip.chatservice.feigns.UserClient;
import tn.sip.chatservice.repositories.ChatRoomRepository;
import tn.sip.chatservice.repositories.MessageRepository;
import tn.sip.chatservice.services.ChatService;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {
    private final ChatRoomRepository chatRoomRepository;
    private final MessageRepository messageRepository;
    private final PropertyClient propertyClient;
    private final UserClient userClient;
    private final NotificationClient notificationClient;

    @Override
    public ChatRoom createOrGetRoom(Long propertyId, Long id1, Long id2) {
        return chatRoomRepository.findByParticipantsAndProperty(propertyId, id1, id2)
                .orElseGet(() -> {
                    ChatRoom room = new ChatRoom();

                    // ⚠️ déterminer correctement qui est client/agence
                    Long clientId = Math.min(id1, id2); // par exemple
                    Long agencyId = Math.max(id1, id2); // à adapter si tu veux vérifier les rôles réels

                    room.setClientId(clientId);
                    room.setAgencyId(agencyId);
                    room.setPropertyId(propertyId);
                    room.setMessages(new ArrayList<>());

                    return chatRoomRepository.save(room);
                });
    }


    @Override
    public Message sendMessage(Long roomId, Long senderId, String senderType, String content) {
        ChatRoom room = chatRoomRepository.findById(roomId)
                .orElseThrow(() -> new RuntimeException("Room not found"));

        Message message = new Message();
        message.setSenderId(senderId);
        message.setSenderType(senderType);
        message.setContent(content);
        message.setChatRoom(room);

        Message savedMessage = messageRepository.save(message);

        // 🔔 Déterminer le destinataire
        Long receiverId;
        String receiverType;
        if ("CLIENT".equalsIgnoreCase(senderType)) {
            receiverId = room.getAgencyId();
            receiverType = "AGENCY";
        } else {
            receiverId = room.getClientId();
            receiverType = "CLIENT";
        }

        // 🔔 Notification
        NotificationRequest notificationRequest = new NotificationRequest();
        notificationRequest.setUserId(receiverId);
        notificationRequest.setMessage("Vous avez reçu un nouveau message : " + content);
        notificationRequest.setUrl("/chat/room/" + room.getId());

        notificationClient.sendNotification(notificationRequest);

        return savedMessage;
    }

    @Override
    public Optional<ChatRoom> getChatRoomById(Long id) {
        return chatRoomRepository.findById(id);
    }

    @Override
    public List<Message> getMessages(Long roomId) {
        return messageRepository.findByChatRoomIdOrderByTimestampAsc(roomId);
    }

    @Override
    public Long getAgencyIdByProperty(Long propertyId) {
        return chatRoomRepository.findFirstByPropertyId(propertyId)
                .map(ChatRoom::getAgencyId)
                .orElseThrow(() -> new RuntimeException("Aucune agence trouvée pour le bien " + propertyId));
    }

    @Override
    public Long getAgencyIdByPropertyId(Long propertyId) {
        return propertyClient.getAgencyIdByPropertyId(propertyId);
    }

    @Override
    public List<ChatRoom> getRoomsByAgency(Long agencyId) {
        return chatRoomRepository.findByAgencyId(agencyId);
    }

    @Override
    public List<ChatRoom> getChatRoomsForClient(Long clientId) {
        return chatRoomRepository.findByClientId(clientId);
    }
    @Override
    public Optional<ChatRoom> findByPropertyIdAndAgencyId(Long propertyId, Long agencyId) {
        return chatRoomRepository.findByPropertyIdAndAgencyId(propertyId, agencyId);
    }

    @Override
    public Map<String, String> getUserNames(Long clientId, Long agencyId) {
        UserDTO client = userClient.getUserById(clientId);
        UserDTO agency = userClient.getUserById(agencyId);

        Map<String, String> names = new HashMap<>();
        names.put("clientName", (client.getFirstName() + " " + client.getLastName()).trim());
        if (agency.getAgencyDTO() != null && agency.getAgencyDTO().getAgencyName() != null) {
            names.put("agencyName", agency.getAgencyDTO().getAgencyName());
        } else {
            names.put("agencyName", (agency.getFirstName() + " " + agency.getLastName()).trim());
        }

        return names;
    }

}
