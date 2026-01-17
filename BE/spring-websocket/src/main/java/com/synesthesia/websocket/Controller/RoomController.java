package com.synesthesia.websocket.Controller;

import com.synesthesia.websocket.Repository.RoomRepository;
import com.synesthesia.websocket.Repository.UserRepository;
import com.synesthesia.websocket.dto.RoomRequest;
import com.synesthesia.websocket.dto.RoomResponse;
import com.synesthesia.websocket.entity.Message;
import com.synesthesia.websocket.entity.Room;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/v1/rooms")
public class RoomController {

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> createRoom(@RequestBody RoomRequest request) {
        String sender = request.getUsername();
        String receiver = request.getRoomId();

        List<String> roomIds = "personal".equals(request.getType())
                ? List.of(sender + "#" + receiver, receiver + "#" + sender)
                : List.of(receiver);

        if (!roomRepository.findByRoomIdIn(roomIds).isEmpty()) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("Room already exists");
        }

        String finalRoomId = roomIds.getFirst();
        request.setRoomId(finalRoomId);

        Room newRoom = new Room();
        newRoom.setRoomId(finalRoomId);
        newRoom.setMessages(new ArrayList<>());
        roomRepository.save(newRoom);

        if ("personal".equals(request.getType())) {
            List<String> usernamesToUpdate = List.of(sender, receiver);
            usernamesToUpdate.forEach(username -> addRoomToUser(username, finalRoomId, "personal"));
        }else {
            addRoomToUser(sender, finalRoomId, "group");
        }

        return ResponseEntity.ok(new RoomResponse(finalRoomId, request.getType()));
    }

    @GetMapping
    public ResponseEntity<List<RoomResponse>> getRooms() {

        List<RoomResponse> rooms = new ArrayList<>();

        userRepository.findAll().forEach( room -> {
            rooms.add(new RoomResponse(room.getUsername(), "personal"));
        });

        roomRepository.findAllByType("group").forEach( room -> {
            rooms.add(new RoomResponse(room.getRoomId(), "group"));
        });

        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<?> getRoom(@PathVariable String roomId) {
        Optional<Room> byRoomId = roomRepository.findByRoomId(roomId);
        if (byRoomId.isEmpty()) {
            return ResponseEntity.badRequest().body("Room not found");
        }

        return ResponseEntity.ok(byRoomId.get());
    }

    @GetMapping("/{roomId}/messages")
    public ResponseEntity<Object> getMessages(
            @PathVariable String roomId,
            @RequestParam(defaultValue = "1", required = false) int page,
            @RequestParam(defaultValue = "20", required = false) int size
        ) {

        Optional<Room> byRoomId = roomRepository.findByRoomId(roomId);
        if (byRoomId.isEmpty()) {
            return ResponseEntity.badRequest().body("Room not found");
        }

        List<Message> messages = byRoomId.get().getMessages();
        int fromIndex = (page-1) * size;
        if (fromIndex >= messages.size()) {
            return ResponseEntity.ok(List.of());
        }
        int toIndex = Math.min(fromIndex + size, messages.size());

        return ResponseEntity.ok(messages.subList(fromIndex, toIndex));
    }

    private void addRoomToUser(String username, String roomId, String type) {
        userRepository.findByUsername(username).ifPresent(user -> {
            Map<String, String> rooms = user.getRooms();
            if (rooms == null) rooms = new HashMap<>();
            rooms.put(roomId, type);
            user.setRooms(rooms);
            userRepository.save(user);
        });
    }

}
