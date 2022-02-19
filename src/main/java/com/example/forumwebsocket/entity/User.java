package com.example.forumwebsocket.entity;

import com.example.forumwebsocket.models.UserRequest;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.example.forumwebsocket.helpers.ConstantRoles.ROLE_ADMIN;
import static javax.persistence.FetchType.EAGER;

@Entity
@Table(name = "users")
@Data
@ToString
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(unique = true)
    private String email;
    @Column(unique = true)
    private String userName;
    @Column(columnDefinition = "Text")
    private String description;
    @ToString.Exclude
    private String password;
    @ManyToMany(fetch = EAGER)
    private Collection<Role> roles = new ArrayList<>();
    @OneToMany(mappedBy = "user")
    @ToString.Exclude
    private List<Post> posts;
    @ManyToMany
    @ToString.Exclude
    private List<User> friends;
    private int postPrivacy;

    public static int VISIBLE_ALL = 1;
    public static int VISIBLE_FRIENDS = 2;

    public User(String email, String userName, String description, String password) {
        this.email = email;
        this.userName = userName;
        this.description = description;
        this.password = password;
    }

    public User(UserRequest request) {
        this.email = request.getEmail();
        this.userName = request.getUserName();
        this.description = request.getDescription();
        this.password = request.getPassword();
    }

    public boolean isAdmin() {
        return roles.contains(ROLE_ADMIN);
    }

    public void addFriend(User friend) {
        friends.add(friend);
    }

    public void deleteFriend(User friend) {
        friends.remove(friend);
    }
}
