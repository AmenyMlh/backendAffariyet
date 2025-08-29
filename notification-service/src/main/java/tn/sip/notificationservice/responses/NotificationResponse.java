package tn.sip.notificationservice.responses;


import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NotificationResponse {
    private Long id;
    private String message;
    private LocalDateTime createdDate;
    private boolean seen;
    private Long userId;
    private Boolean verified;
    private String url;

}
