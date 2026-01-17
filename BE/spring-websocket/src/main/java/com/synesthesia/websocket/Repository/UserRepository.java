package com.synesthesia.websocket.Repository;

import com.synesthesia.websocket.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> getByUsername(String username);
    Optional<User> findByUsername(String username);

}
