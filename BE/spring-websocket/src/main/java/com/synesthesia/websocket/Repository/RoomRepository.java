package com.synesthesia.websocket.Repository;

import com.synesthesia.websocket.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoomRepository extends MongoRepository<Room, String> {

    Optional<Room> findByRoomId(String roomId);

    List<Room> findAllByType(String type);

    List<Room> findByRoomIdIn(List<String> roomIds);

}
