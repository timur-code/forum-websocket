package com.example.forumwebsocket.Services;

import com.example.forumwebsocket.entity.FriendRequest;
import com.example.forumwebsocket.entity.Role;
import com.example.forumwebsocket.entity.User;
import com.example.forumwebsocket.helpers.UserFromToken;
import com.example.forumwebsocket.logs.Log;
import com.example.forumwebsocket.logs.UserLogs;
import com.example.forumwebsocket.models.UserRequest;
import com.example.forumwebsocket.repository.*;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.management.InstanceAlreadyExistsException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static com.example.forumwebsocket.helpers.ConstantRoles.ROLE_ADMIN;

@Service
@Transactional
@Slf4j
public class UserService implements UserDetailsService{
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private FriendRequestRepository friendRequestRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean checkIfAdmin(Long userId) {
        //Check if Exists beforehand
        Optional<User> userById = userRepository.findById(userId);
        return userById.get().getRoles().contains(ROLE_ADMIN);
    }

    public boolean checkIfExists(Long userId) {
        Optional<User> userById = userRepository.findById(userId);
        return userById.isPresent();
    }

    public User getUserFromToken(String token) throws NotFoundException{
        String userName = UserFromToken.getUsernameFromToken(token);
        try {
            return getUser(userName);
        } catch (NotFoundException ex)
        {
            throw ex;
        }
    }

    public User getUserFromToken(HttpServletRequest request) throws NotFoundException{
        Cookie[] cookies = request.getCookies();
        for(int i = 0; i < cookies.length; i++) {
            if(cookies[i].getName().equals("Authorization")) {
                String userName = UserFromToken.getUsernameFromToken(cookies[i].getValue());
                return getUser(userName);
            }
        }
        throw new NotFoundException("Token was not found.");

    }

    public void editEmail(String email, Long userId) throws NotFoundException {
        try {
            User user = getUser(userId);
            user.setEmail(email);
            userRepository.save(user);
        } catch (NotFoundException ex) {
            throw ex;
        }
    }

    public void editPassword(String password, Long userId) throws NotFoundException {
        try {
            User user = getUser(userId);
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
            password = encoder.encode(password);
            user.setPassword(password);
            userRepository.save(user);
        } catch (NotFoundException ex) {
            throw ex;
        }
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUserName(username);
        if(user == null) {
            log.error("User not found in the database");
            throw new UsernameNotFoundException("User not found in the database");
        } else {
            log.info("User found in the database: {}", username);
            Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
            user.getRoles().forEach(role -> {
                authorities.add(new SimpleGrantedAuthority(role.getName()));
            });
            return new org.springframework.security.core.userdetails.User(user.getUserName(), user.getPassword(), authorities);
        }
    }

    public void addUser(UserRequest request) throws InstanceAlreadyExistsException {
        if(userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new InstanceAlreadyExistsException();
        }
        User user = new User(request);
        String pass = passwordEncoder.encode(user.getPassword());
        System.out.println(pass);
        user.setPassword(pass);
        Role user_role = roleRepository.findByName("ROLE_USER");
        user.getRoles().add(user_role);
        userRepository.save(user);
    }

    public void sendFriendRequest(Long senderId, Long receiverId) {
        FriendRequest friendRequest = new FriendRequest(userRepository.findById(senderId).get(),
                                                        userRepository.findById(receiverId).get());
        log.info(friendRequest.toString());
        friendRequestRepository.save(friendRequest);
    }

    public void acceptFriendRequest(Long requestId) {
        FriendRequest friendRequest = friendRequestRepository.findById(requestId).get();
        User receiver = friendRequest.getReceiver();
        User sender = friendRequest.getSender();
        receiver.addFriend(sender);
        sender.addFriend(receiver);
        userRepository.save(sender);
        userRepository.save(receiver);
        friendRequestRepository.deleteById(requestId);
    }

    public List<FriendRequest> getFriendRequestsByUser(Long userId) {
        return friendRequestRepository.findAllByReceiver_Id(userId);
    }


    public Role saveRole(Role role) {
        return roleRepository.save(role);
    }

    public void addRoleToUser(String username, String roleName) {
        log.info("Adding role {} to user {}", roleName, username);
        User user = userRepository.findByUserName(username);
        Role role = roleRepository.findByName(roleName);
        user.getRoles().add(role);
        userRepository.save(user);
    }

    /*public void addGroupToUser(User user, Group group){
        user.getSubscribedGroups().add(group);
        userRepository.save(user);

    }*/

    public User getUser(Long userId) throws NotFoundException {
        Optional<User> user = userRepository.findById(userId);

        if(user.isEmpty())
            throw new NotFoundException("User was not found.");

        return user.get();
    }

    public User getUser(String userName) throws NotFoundException {
        User user = userRepository.findByUserName(userName);

        if(user == null)
            throw new NotFoundException("User was not found.");

        return user;
    }

    public void logout(HttpServletResponse response) {
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
    }

    public ArrayList<User> getUsers() {
        log.info("Fetching all users");
        return userRepository.findAll();
    }

    public ArrayList<Log> getUserLogs() {
        return UserLogs.getUserLogs();
    }
}
