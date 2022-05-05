package com.example.boardservice.outbound.web.rest;

import com.example.boardservice.inbound.service.PostService;
import com.example.boardservice.outbound.dto.PostResource;
import com.example.boardservice.outbound.dto.PostResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
public class PostController {

    private final PostService postService;

    @GetMapping("/users/{userId}/posts")
    public ResponseEntity<List<PostResponse>> getPostsByUserId(@PathVariable String userId) {
        return ResponseEntity.ok(postService.findByUserId(userId));
    }

    @PostMapping("/users/{userId}/posts")
    public ResponseEntity<PostResponse> savePost(@PathVariable String userId,
                                                 @RequestBody PostResource postResource) {
        return ResponseEntity.status(HttpStatus.CREATED).body(postService.savePost(userId, postResource));
    }

    @PutMapping("/users/{userId}/posts/{postId}")
    public ResponseEntity<PostResponse> updatePost(@PathVariable String userId,
                                                     @PathVariable String postId,
                                                     @RequestBody PostResource postResource) {
        return ResponseEntity.ok(postService.updatePost(userId, postId, postResource));
    }

}
