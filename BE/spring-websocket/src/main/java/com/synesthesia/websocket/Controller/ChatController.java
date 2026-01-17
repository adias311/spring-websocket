package com.synesthesia.websocket.Controller;

import com.synesthesia.websocket.Repository.RoomRepository;
import com.synesthesia.websocket.dto.RoomResponse;
import com.synesthesia.websocket.dto.MessageRequest;
import com.synesthesia.websocket.entity.Message;
import com.synesthesia.websocket.entity.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


import java.time.LocalDateTime;
import java.util.List;

@Controller
public class ChatController {

    private RoomRepository roomRepository;
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ChatController(
            RoomRepository roomRepository,
            SimpMessagingTemplate messagingTemplate
    ) {
        this.roomRepository = roomRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/sendMessage")
    public void sendMessage(MessageRequest request) {

        roomRepository.findByRoomId(request.getRoomId())
            .ifPresentOrElse(
                room -> {
                    Message savedMessage = new Message();
                    savedMessage.setSender(request.getSender());
                    savedMessage.setContent(request.getContent());
                    savedMessage.setTimeStamp(LocalDateTime.now());

                    room.getMessages().add(savedMessage);
                    roomRepository.save(room);

                    System.out.println(request.getRoomId());

                    messagingTemplate.convertAndSend("/topic/room/"+request.getRoomId(), savedMessage);
                },
                () -> System.out.println("Room not found")
            );
    }

}
