package com.example.forumwebsocket.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class UserRequest {
    private String email;
    private String userName;
    private String description;
    private String password;
}
