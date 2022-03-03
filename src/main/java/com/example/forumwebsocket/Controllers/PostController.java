package com.example.forumwebsocket.Controllers;

import com.example.forumwebsocket.Services.PostService;
import com.example.forumwebsocket.Services.UserService;
import com.example.forumwebsocket.entity.Comment;
import com.example.forumwebsocket.entity.Post;
import com.example.forumwebsocket.entity.User;
import com.example.forumwebsocket.models.CommentRequest;
import com.example.forumwebsocket.models.PostRequest;
import javassist.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/post")
public class PostController {
    private PostService postService;
    private UserService userService;

    @Autowired
    public PostController(PostService postService, UserService userService) {
        this.postService = postService;
        this.userService = userService;
    }

    @PostMapping("/create")
    public String createPost(@ModelAttribute("postRequest") PostRequest postRequest, HttpServletRequest request)
    {
        Long user = postRequest.getUserId();
        try {
            Long postId = postService.addPost(user, postRequest);
            return "redirect:/post/" + postId;
        } catch (Exception ex) {
            log.error("Couldn't open a page");
            return "error";
        }

    }

    @GetMapping("/create")
    public String createPostPage(Model model, HttpServletRequest request) {
        return "createPost";
    }

    @ModelAttribute(value = "postRequest")
    public PostRequest newPostRequest()
    {
        return new PostRequest();
    }

    @GetMapping("/{postId}")
    public String getPost(@PathVariable(value = "postId") Long postId, Model model, HttpServletRequest request) {
        log.info(request.toString());
        try {
            Post post = postService.returnPost(postId);

            if(post.getVisibility() == Post.VISIBLE_FRIENDS)
                if(!post.getUser().getFriends().contains(userService.getUserFromToken(request)) && post.getUser() != userService.getUserFromToken(request)) {
                    log.error("Access denied");
                    return "accessDenied";
                }

            if(!post.isAreCommentsDisabled()) {
                List<Comment> comments = postService.getAllCommentsToPost(postId);
                model.addAttribute("comments", comments);
            }
            model.addAttribute(post);
            return "post";
        } catch (NotFoundException ex) {
            log.error("Couldn't open a page");
            return "error";
        }
    }

    @PostMapping("/{postId}/addComment")
    public String addComment(@PathVariable(value = "postId") Long postId, CommentRequest commentRequest, Model model, HttpServletRequest request) {
        log.info(request.toString());
        try {
            Post post = postService.returnPost(postId);
            if(post.isAreCommentsDisabled()) {
                log.error("Cannot comment on this post");
                return "error";
            }
            postService.addCommentToPost(commentRequest);
            List<Comment> comments = postService.getAllCommentsToPost(postId);
            model.addAttribute(post);
            model.addAttribute("comments", comments);
            return "redirect:/post/" + postId;
        } catch (Exception ex) {
            return "error";
        }
    }

    @GetMapping("/{postId}/edit")
    public String getEditPost(@PathVariable(value = "postId") Long postId, Model model, HttpServletRequest request) {
        log.info(request.toString());
        try {
            Post post = postService.returnPost(postId);
            model.addAttribute(post);
            return "editPostPage";
        } catch (NotFoundException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error";
        }
    }

    @PostMapping("/{postId}/edit")
    public String editPost(@PathVariable(value = "postId") Long postId, @RequestParam String postText, @RequestParam String title, HttpServletRequest request, Model model) {
        User user;
        try {
            user = userService.getUserFromToken(request);
        } catch (NotFoundException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            return "error";
        }
        try {
            postService.editPost(postId, title, postText, user);
            return "redirect:/";
        } catch (Exception ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            log.error("Couldn't open a page");
            return "error";
        }
    }

    @PostMapping("{postId}/delete")
    public String deletePost(@PathVariable(value = "postId") Long postId, HttpServletRequest request, Model model) {
        User user;
        try {
            user = userService.getUserFromToken(request);
        } catch (NotFoundException ex) {
            model.addAttribute("errorMessage", ex.getMessage());
            log.error("Couldn't open a page");
            return "error";
        }
        try {
            postService.deletePost(postId, user);
            return "redirect:/user/";
        } catch (Exception ex) {
            log.error("Couldn't open a page");

            return "error";
        }
    }

    /*@PatchMapping("/edit")
    public ResponseEntity editPost(@RequestHeader Long postId, @RequestBody PostRequest postRequest) {
        try {
            postService.editPost(postId, postRequest);
            return ResponseEntity.ok("Post was edited successfully.");
        } catch (NotFoundException ex) {
            return ResponseEntity.badRequest().body(ex);
        }
    }*/
}
