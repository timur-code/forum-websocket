package com.example.forumwebsocket.Controllers;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.example.forumwebsocket.Services.PostService;
import com.example.forumwebsocket.Services.UserService;
import com.example.forumwebsocket.entity.FriendRequest;
import com.example.forumwebsocket.entity.Post;
import com.example.forumwebsocket.entity.Role;
import com.example.forumwebsocket.entity.User;
import com.example.forumwebsocket.models.FriendRequestForm;
import com.example.forumwebsocket.models.RoleToUserForm;
import com.example.forumwebsocket.models.UserRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.*;

import javax.management.InstanceAlreadyExistsException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.example.forumwebsocket.helpers.ConstantRoles.ROLE_ADMIN;
import static org.springframework.http.HttpStatus.FORBIDDEN;

@Slf4j
@Controller
public class UserController {
    private UserService userService;
    private PostService postService;

    @Autowired
    public UserController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @GetMapping("/user/create")
    public String createUser() {
        return "registration";
    }

    @PostMapping("/user/create")
    public String createUser(@ModelAttribute("userRequest") UserRequest request, Model model, HttpServletRequest httpServletRequest) {
        log.info(httpServletRequest.toString());
        try {
            userService.addUser(request);
            return "redirect:/login";
        } catch (InstanceAlreadyExistsException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            log.error("Couldn't open a page");
            return "error";
        }
    }

    @ModelAttribute(value = "userRequest")
    public UserRequest newUserRequest()
    {
        return new UserRequest();
    }

    @GetMapping("/")
    public String defaultPage(Model model, HttpServletRequest request) {
        log.info(request.toString());
        User user;
        try {
            user = userService.getUserFromToken(request);
            List<Post> posts = new ArrayList<>();
            List<User> friends = user.getFriends();
            for(User friend : friends) {
                posts.addAll(postService.getAllPostsByUser(friend.getId()));
            }
            model.addAttribute("posts", posts);
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error";
        }
        model.addAttribute("user", user);

        if (user.getRoles().contains(ROLE_ADMIN))
            model.addAttribute("admin", true);
        else
            model.addAttribute("admin", false);
        return "homePage";
    }

    @GetMapping("/user/{userId}")
    public String getUser(@PathVariable(value = "userId") Long userId, Model model, HttpServletRequest request) {
        log.info(request.toString());
        try {
            User user = userService.getUser(userId);
            model.addAttribute("user", user);
            return "userPage";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            log.error("Couldn't open a page");
            return  "error";
        }
    }

    @GetMapping("/user/{userId}/friendRequests")
    public String getFriendRequests(@PathVariable(value = "userId") Long userId, Model model, HttpServletRequest request) {
        log.info(request.toString());
        try {
            List<FriendRequest> friendRequests = userService.getFriendRequestsByUser(userId);
            User user = userService.getUser(userId);
            model.addAttribute("user", user);
            model.addAttribute("friendRequests" ,friendRequests);
            return "friendRequests";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            log.error("Couldn't open a page");
            return  "error";
        }
    }

    @PostMapping("/user/{userId}/friendRequests/{friendRequestId}/add")
    public String acceptFriendRequest(@PathVariable(value = "userId") Long userId, @PathVariable(value = "friendRequestId") Long friendRequestId, HttpServletRequest request) {
        log.info(request.toString());
        userService.acceptFriendRequest(friendRequestId);
        return "friendRequests";
    }

    @PostMapping("/user/{userId}/addFriend")
    public String addFriend(@PathVariable(value = "userId") Long userId, @ModelAttribute("friendRequest") FriendRequestForm friendRequestForm, Model model, HttpServletRequest request) {
        log.info(request.toString());
        try {
            User user = userService.getUser(userId);
            userService.sendFriendRequest(friendRequestForm.getSender(), friendRequestForm.getReceiver());
            model.addAttribute("user", user);
            return "userPage";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            log.error("Couldn't open a page");
            return  "error";
        }
    }

    @GetMapping("/user/{userId}/edit")
    public String editUserPage(@PathVariable(value = "userId") Long userId, Model model, HttpServletRequest request) {
        log.info(request.toString());
        try {
            User user = userService.getUser(userId);
            model.addAttribute("user", user);
            return "editUserPage";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            log.error("Couldn't open a page");

            return  "error";
        }
    }

    @PostMapping("/user/{userId}/editPassword")
    public String editUserPassword(@PathVariable(value = "userId") Long userId, @RequestParam String password, Model model, HttpServletRequest request) {
        log.info(request.toString());
        try {
            userService.editPassword(password, userId);
            return "redirect:/user/logout";
        } catch (NotFoundException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            log.error("Couldn't open a page");

            return  "error";
        }
    }

    @PostMapping("/user/{userId}/editEmail")
    public String editUserEmail(@PathVariable(value = "userId") Long userId, @RequestParam String email, Model model, HttpServletRequest request) {
        log.info(request.toString());
        try {
            userService.editEmail(email, userId);
            return "redirect:/";
        } catch (NotFoundException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            log.error("Couldn't open a page");
            return  "error";
        }
    }

    @GetMapping("/user/{userId}/posts")
    public String getPosts(@PathVariable(value = "userId") Long userId, Model model, HttpServletRequest request) {
        log.info(request.toString());
        try {
            List<Post> posts = postService.getAllPostsByUser(userId);
            model.addAttribute("posts", posts);
            User user = userService.getUser(userId);
            User currentUser = userService.getUserFromToken(request);
            if(user.getPostPrivacy() == User.VISIBLE_FRIENDS && !user.getFriends().contains(currentUser))
                return "accessDenied";

            return "userPosts";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex);
            return "error";
        }
    }

    /*@PostMapping("/role/save")
    public ResponseEntity<Role>saveRole(@RequestBody Role role) {
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/user/role/save").toUriString());
        return ResponseEntity.created(uri).body(userService.saveRole(role));
    }*/

    @GetMapping("/admin/role/addtouser")
    public String addRoleToUser() {
        return "addRoleToUser";
    }

    @PostMapping("/admin/role/addtouser")
    public String addRoleToUser(@ModelAttribute("roleToUserForm") RoleToUserForm form) {
        userService.addRoleToUser(form.getUsername(), form.getRoleName());
        return "redirect:/user/";
    }

    @ModelAttribute(value = "roleToUserForm")
    public RoleToUserForm newRoleToUserForm()
    {
        return new RoleToUserForm();
    }

    @GetMapping("/user/logout")
    public String logout(HttpServletRequest request,HttpServletResponse response) {
        log.info(request.toString());
        userService.logout(response);
        log.info(response.toString());
        return "redirect:/login";
    }

    @GetMapping("/admin")
    public String getAdminPanel() {
        return "adminPanel";
    }

    @GetMapping("/admin/viewUsers")
    public String viewUsers(Model model) {
        model.addAttribute("users" ,userService.getUsers());
        return "viewUsers";
    }

    @GetMapping("/admin/viewUserLogs")
    public String viewUSerLogs(Model model) {
        model.addAttribute("userLogs", userService.getUserLogs());
        return "viewUserLogs";
    }

    @GetMapping("/user/all")
    public String viewAllUsers (Model model, HttpServletRequest request) {
        log.info(request.toString());
        model.addAttribute("users" ,userService.getUsers());
        return "viewUsers";
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    public void handleNotFoundError(NotFoundException exception) {
        log.error(String.valueOf(exception));
    }

    @ExceptionHandler(PermissionDeniedDataAccessException.class)
    public void handlePermissionDenied(PermissionDeniedDataAccessException exception) {
        log.error(String.valueOf(exception));
    }

    @GetMapping("/user/token/refresh")
    public void refreshToken(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Cookie[] cookies = request.getCookies();
        String authorizationHeader = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().equals("Refresh"))
                authorizationHeader = cookie.getValue();
        }
        if(authorizationHeader != null) {
            try {
                String refresh_token = authorizationHeader;
                Algorithm algorithm = Algorithm.HMAC256("secret".getBytes());
                JWTVerifier verifier = JWT.require(algorithm).build();
                DecodedJWT decodedJWT = verifier.verify(refresh_token);
                String username = decodedJWT.getSubject();
                User user = userService.getUser(username);
                String access_token = JWT.create()
                        .withSubject(user.getUserName())
                        .withExpiresAt(new Date(System.currentTimeMillis() + 10 * 60 * 1000))
                        .withIssuer(request.getRequestURL().toString())
                        .withClaim("roles", user.getRoles().stream().map(Role::getName).collect(Collectors.toList()))
                        .sign(algorithm);
                Cookie authorization = new Cookie("Authorization", access_token);
                authorization.setDomain("localhost");
                authorization.setPath("/");
                response.addCookie(authorization);
                Cookie refresh = new Cookie("Refresh", refresh_token);
                refresh.setDomain("localhost");
                refresh.setPath("/");
                response.addCookie(refresh);
                String redirectURL = "http://" + request.getServerName() + ":8080/";
                response.sendRedirect(redirectURL);
            }catch (Exception exception) {
                Cookie authorization = new Cookie("Authorization", null);
                authorization.setDomain("localhost");
                authorization.setPath("/");
                authorization.setMaxAge(0);
                response.addCookie(authorization);

                Cookie JSESSIONID = new Cookie("JSESSIONID", null);
                authorization.setDomain("localhost");
                authorization.setPath("/");
                authorization.setMaxAge(0);
                response.addCookie(JSESSIONID);

                Cookie refresh = new Cookie("Refresh", null);
                authorization.setDomain("localhost");
                authorization.setPath("/");
                authorization.setMaxAge(0);
                response.addCookie(refresh);

                response.sendRedirect("http://localhost:8080/login");

                response.setHeader("error", exception.getMessage());
                response.setStatus(FORBIDDEN.value());
                Map<String, String> error = new HashMap<>();
                error.put("error_message", exception.getMessage());
                response.setContentType(MimeTypeUtils.APPLICATION_JSON_VALUE);
                new ObjectMapper().writeValue(response.getOutputStream(), error);
            }
        } else {
            throw new RuntimeException("Refresh token is missing");
        }
    }
}
