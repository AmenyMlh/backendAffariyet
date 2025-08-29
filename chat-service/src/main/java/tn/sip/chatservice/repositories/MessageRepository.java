package tn.sip.chatservice.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import tn.sip.chatservice.entities.Message;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChatRoomIdOrderByTimestampAsc(Long chatRoomId);

    List<Message> findByChatRoomId(Long chatRoomId);

}
