package com.example.forumwebsocket.Controllers;

import com.example.forumwebsocket.Services.UserService;
import com.example.forumwebsocket.entity.User;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
public class Advice {
    private UserService userService;

    @Autowired
    public Advice(UserService userService) {
        this.userService = userService;
    }

    // adds a global value to every model
    @ModelAttribute("currentUser")
    public User currentUser(HttpServletRequest request) {
        User user;
        try {
            user = userService.getUserFromToken(request);
            return user;
        } catch (NotFoundException ex) {
            //System.out.println("Couldn't get current user");
            return null;
        }
    }
}