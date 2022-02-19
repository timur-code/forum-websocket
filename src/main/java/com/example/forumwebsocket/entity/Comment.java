package com.example.forumwebsocket.entity;

import com.example.forumwebsocket.models.CommentRequest;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "post_id")
    @JsonBackReference
    private Post post;
    @Column(columnDefinition = "varchar")
    private String commentText;

    public Comment(CommentRequest commentRequest) {
        this.commentText = commentRequest.getCommentText();
    }
}
