package com.example.forumwebsocket.entity;

//import com.example.forumwebsocket.models.PostRequest;
import com.example.forumwebsocket.models.PostRequest;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;

@Entity
@Table(name = "posts")
@Getter
@Setter
@NoArgsConstructor
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String title;
    @Column(columnDefinition = "varchar")
    private String postText;
    private LocalDate postDate;
    private int visibility;
    private boolean areCommentsDisabled = false;
    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;


    public static int VISIBLE_ALL = 1;
    public static int VISIBLE_FRIENDS = 2;

    public Post(String title, String postText, int visibility) {
        this.title = title;
        this.postText = postText;
        this.visibility = visibility;
        this.postDate = LocalDate.now();
    }

    public Post(PostRequest request) {
        this.title = request.getTitle();
        this.postText = request.getPostText();
        this.visibility = request.getVisibility();
        this.areCommentsDisabled = request.isAreCommentsDisabled();
        this.postDate = LocalDate.now();
    }

    public void editPost(PostRequest request) {
        this.title = request.getTitle();
        this.postText = request.getPostText();
        this.postDate = LocalDate.now();
    }

    @Override
    public String toString() {
        return "Post{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", postText='" + postText + '\'' +
                ", postDate=" + postDate +
                '}';
    }
}
