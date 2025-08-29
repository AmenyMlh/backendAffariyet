package tn.sip.chatservice.dto;

import lombok.Data;

@Data
public class MessageDTO {
    private Long roomId;
    private Long senderId;
    private String senderType;
    private String content;
}
