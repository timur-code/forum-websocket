package com.example.forumwebsocket.repository;

import com.example.forumwebsocket.entity.Comment;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CommentRepository extends CrudRepository<Comment, Long> {
    List<Comment> findAllByPost_Id(Long postId);

}
