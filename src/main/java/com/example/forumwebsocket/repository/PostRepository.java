package com.example.forumwebsocket.repository;

import com.example.forumwebsocket.entity.Post;
import com.example.forumwebsocket.entity.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PostRepository extends CrudRepository<Post, Long> {
    Post findByTitle(String title);

    void deleteById(Long postId);

    List<Post> findAllByUser(User user);
}
