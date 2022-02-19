package com.example.forumwebsocket.Services;

import com.example.forumwebsocket.entity.Comment;
import com.example.forumwebsocket.entity.Post;
import com.example.forumwebsocket.entity.User;
import com.example.forumwebsocket.models.CommentRequest;
import com.example.forumwebsocket.models.PostRequest;
import com.example.forumwebsocket.repository.CommentRepository;
import com.example.forumwebsocket.repository.PostRepository;
import com.example.forumwebsocket.repository.UserRepository;
import javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private CommentRepository commentRepository;

    public Long addPost(Long userId, PostRequest request) throws Exception{
        Post newPost = new Post(request);
        try {
            addUserToPost(userId, newPost);
            addPostToUser(userId, newPost);
            postRepository.save(newPost);
            return newPost.getId();
        } catch (Exception ex) {
            throw ex;
        }
    }

    /*private boolean containsPost(PostRequest request) {
        Optional<Post> postById = postRepository.findById(request.getId());
        return postById.isPresent();
    }*/

    public void editPost(Long postId, String title, String postText, User user) throws Exception{
        try {
            Post post = returnPost(postId);
            if (post.getUser().equals(user) || userService.checkIfAdmin(user.getId())) {
                post.setPostText(postText);
                post.setTitle(title);
                postRepository.save(post);
            } else {
                throw new Exception("Not the creator or admin, so cannot edit");
            }
        } catch (NotFoundException ex) {
            throw ex;
        }
    }

    public List<Post> getAllPostsByUser(Long userId) throws Exception{
        try {
            User user = userService.getUser(userId);
            return postRepository.findAllByUser(user);
        } catch (Exception ex) {
            throw ex;
        }

    }

    private boolean containsPostById(Long postId) {
        Optional<Post> postById = postRepository.findById(postId);
        return postById.isPresent();
    }

    private void addUserToPost(Long userId, Post post) throws Exception{
        Optional<User> userById = userRepository.findById(userId);
        if(userById.isEmpty())
            throw new Exception("User does not exist.");

        post.setUser(userById.get());
        postRepository.save(post);
    }

    private void addPostToUser(Long userId, Post post) throws Exception{
        Optional<User> userById = userRepository.findById(userId);
        if(userById.isEmpty())
            throw new Exception("User does not exist.");

        userById.get().getPosts().add(post);
        userRepository.save(userById.get());
    }

    public List<Comment> getAllCommentsToPost(Long postId) {
        return commentRepository.findAllByPost_Id(postId);
    }

    public void addCommentToPost(CommentRequest commentRequest) {
        Comment comment = new Comment(commentRequest);
        comment.setPost(postRepository.findById(commentRequest.getPost()).get());
        commentRepository.save(comment);
    }

    public Post returnPost(Long postId) throws NotFoundException {
        Optional<Post> postById = postRepository.findById(postId);
        if(postById.isEmpty())
            throw new NotFoundException("Post was not found.");
        return postById.get();
    }

    public void deletePost(Long postId, User user) throws Exception{
        if(!containsPostById(postId))
            throw new NotFoundException("Post was not found.");

        Post post = returnPost(postId);
        if (post.getUser().equals(user) || userService.checkIfAdmin(user.getId())) {
            postRepository.deleteById(postId);
        } else {
            throw new Exception("Not the creator or admin, so cannot delete");
        }
    }

    /*public void editPost(Long postId, PostRequest request) throws NotFoundException {
        if(!containsPostById(postId))
            throw new NotFoundException("Post was not found.");
        Post post = postRepository.findById(postId).get();
        post.editPost(request);
        postRepository.save(post);
    }*/
}
