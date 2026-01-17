package com.synesthesia.websocket.Controller;

import com.synesthesia.websocket.Repository.RoomRepository;
import com.synesthesia.websocket.Repository.UserRepository;
import com.synesthesia.websocket.entity.Message;
import com.synesthesia.websocket.entity.Room;
import com.synesthesia.websocket.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoomRepository roomRepository;

    @PostMapping
    public ResponseEntity<User> getOrCreateUser(@RequestBody String username) {

        User byUsername = userRepository.getByUsername(username).orElseGet(() -> {

            Room defaultRoom = roomRepository.save(Room.builder()
                .roomId("default personal #"+username)
                .type("personal")
                .messages(List.of(Message.builder()
                        .sender("system")
                        .content("Welcome to the chat!")
                        .timeStamp(LocalDateTime.now())
                        .build()))
                .build());

            Map<String, String> rooms = new HashMap<>();
            rooms.put(defaultRoom.getRoomId(),defaultRoom.getType());
            rooms.put("default group","group");

            return userRepository.save(User.builder()
                    .username(username)
                    .rooms(rooms)
                    .build());
        });

        return ResponseEntity.ok(byUsername);
    }
}
