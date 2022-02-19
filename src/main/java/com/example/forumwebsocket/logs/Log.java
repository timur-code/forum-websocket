package com.example.forumwebsocket.logs;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log {
    private String userName;
    private LocalDateTime time;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public Log(String userName) {
        this.userName = userName;
        time = LocalDateTime.now();
    }

    public String getUserName() {
        return userName;
    }

    public String getTime() {
        return time.format(timeFormatter);
    }

    @Override
    public String toString() {
        return userName + " logged in at " + time;
    }
}
