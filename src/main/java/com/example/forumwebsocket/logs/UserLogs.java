package com.example.forumwebsocket.logs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class UserLogs {
    private static HashMap<String, Log> userLogs = new HashMap<>();;

    public static void addUserLog(String userName) {
        Log log = new Log(userName);
        userLogs.put(userName, log);
    }

    public static Log getSpecificUserLog(String userName) {
        return userLogs.get(userName);
    }

    public static ArrayList<Log> getUserLogs() {
        // Getting Collection of values from HashMap
        Collection<Log> logs = userLogs.values();

        // Creating an ArrayList of values
        ArrayList<Log> logsList = new ArrayList<>(logs);
        return logsList;
    }
}
