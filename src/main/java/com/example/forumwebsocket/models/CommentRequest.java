package com.example.forumwebsocket.models;

import lombok.Data;

@Data
public class CommentRequest {
    private Long post;
    private String commentText;
}
