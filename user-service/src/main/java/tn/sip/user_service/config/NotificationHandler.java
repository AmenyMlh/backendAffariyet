package tn.sip.user_service.config;

import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

public class NotificationHandler extends TextWebSocketHandler {

    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {
        // Handle incoming messages if needed
        System.out.println("Message received: " + message.getPayload());
    }

    public void sendNotification(String message, WebSocketSession session) throws Exception {
        session.sendMessage(new TextMessage(message));
    }
}

