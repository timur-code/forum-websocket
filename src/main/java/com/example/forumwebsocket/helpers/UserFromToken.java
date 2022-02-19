package com.example.forumwebsocket.helpers;

import com.auth0.jwt.JWT;

public class UserFromToken {
    public static String getUsernameFromToken(String token) {
        String username = JWT.decode(token).getSubject();
        return username;
    }
}
