package com.example.forumwebsocket.repository;

import com.example.forumwebsocket.entity.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Role findByName(String name);
}