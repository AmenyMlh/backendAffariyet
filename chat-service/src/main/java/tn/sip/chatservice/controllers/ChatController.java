package tn.sip.chatservice.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tn.sip.chatservice.dto.MessageDTO;
import tn.sip.chatservice.entities.ChatRoom;
import tn.sip.chatservice.entities.Message;
import tn.sip.chatservice.services.ChatService;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/chats")
@RequiredArgsConstructor
//@CrossOrigin(origins = "http://localhost:4200")
public class ChatController {
    private final ChatService chatService;

    @PostMapping("/room")
    public ChatRoom openRoom(@RequestParam Long propertyId, @RequestParam Long clientId, @RequestParam Long agencyId) {
        return chatService.createOrGetRoom(propertyId, clientId, agencyId);
    }
    @GetMapping("/room/{id}")
    public ResponseEntity<ChatRoom> getChatRoomById(@PathVariable Long id) {
        return ResponseEntity.ok(chatService.getChatRoomById(id)
                .orElseThrow(() -> new RuntimeException("Chat room not found")));
    }

    @PostMapping("/message")
    public Message sendMessage(@RequestBody MessageDTO messageDTO) {
        return chatService.sendMessage(messageDTO.getRoomId(), messageDTO.getSenderId(), messageDTO.getSenderType(), messageDTO.getContent());
    }

    @GetMapping("/messages/{roomId}")
    public List<Message> getMessages(@PathVariable Long roomId) {
        List<Message> messages = chatService.getMessages(roomId);
        return messages != null ? messages : Collections.emptyList();
    }

    @GetMapping("/agency/{agencyId}/chat-rooms")
    public List<ChatRoom> getAgencyChatRooms(@PathVariable Long agencyId) {
        return chatService.getRoomsByAgency(agencyId);
    }

    @GetMapping("/client/{clientId}/chat-rooms")
    public List<ChatRoom> getChatRoomsForClient(@PathVariable Long clientId) {
        return chatService.getChatRoomsForClient(clientId);
    }

    @GetMapping("/property/{propertyId}/agency/{agencyId}")
    public ResponseEntity<ChatRoom> getRoomByPropertyAndAgency(
            @PathVariable Long propertyId,
            @PathVariable Long agencyId) {
        Optional<ChatRoom> room = chatService.findByPropertyIdAndAgencyId(propertyId, agencyId);
        return room.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/usernames/{clientId}/{agencyId}")
    public Map<String, String> getUserNames(
            @PathVariable Long clientId,
            @PathVariable Long agencyId) {

        return chatService.getUserNames(clientId, agencyId);
    }

}
