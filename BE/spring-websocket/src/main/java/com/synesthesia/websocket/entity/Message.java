package com.synesthesia.websocket.entity;

import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    private String sender;

    private String content;

    private LocalDateTime timeStamp = LocalDateTime.now();

}
