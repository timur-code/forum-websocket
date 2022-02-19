package com.example.forumwebsocket.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class PostRequest {
    private String title;
    private String postText;
    private String userId;
    private int visibility;
    private boolean areCommentsDisabled;

}
