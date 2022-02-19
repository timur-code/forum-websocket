package com.example.forumwebsocket.models;

import lombok.Data;

@Data
public class FriendRequestForm {
    private Long sender;
    private Long receiver;
}
