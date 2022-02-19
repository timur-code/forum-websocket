package com.example.forumwebsocket.repository;

import com.example.forumwebsocket.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.ArrayList;
import java.util.Optional;

public interface UserRepository extends CrudRepository<User, Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findById(Long id);

    User findByUserName(String userName);

    @Override
    ArrayList<User> findAll();
}
