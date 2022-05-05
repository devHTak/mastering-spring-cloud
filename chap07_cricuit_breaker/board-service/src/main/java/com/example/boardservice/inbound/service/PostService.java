package com.example.boardservice.inbound.service;

import com.example.boardservice.inbound.entity.Post;
import com.example.boardservice.inbound.repository.PostRepository;
import com.example.boardservice.outbound.dto.PostResource;
import com.example.boardservice.outbound.dto.PostResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class PostService {

    private final PostRepository postRepository;

    public List<PostResponse> findByUserId(String userId) {
        List<Post> posts = postRepository.findByUserId(userId);
        return posts.stream()
                .map(post -> {
                    return PostResponse.builder()
                            .postId(post.getPostId())
                            .postName(post.getPostName())
                            .description(post.getDescription())
                            .createdAt(post.getCreatedAt())
                            .modifiedAt(post.getModifiedAt()).build();
                }).collect(Collectors.toList());
    }

    public PostResponse savePost(String userId, PostResource postResource) {
        Post post = Post.builder()
                .postId(UUID.randomUUID().toString())
                .userId(userId)
                .postName(postResource.getPostName())
                .description(postResource.getDescription())
                .createdAt(LocalDateTime.now()).build();
        Post returnValue = postRepository.save(post);

        return PostResponse.builder()
                .postId(returnValue.getPostId())
                .postName(returnValue.getPostName())
                .description(returnValue.getDescription())
                .createdAt(returnValue.getCreatedAt()).build();
    }

    public PostResponse updatePost(String userId, String postId, PostResource postResource) {
        Post returnValue = postRepository.findByUserIdAndPostId(userId, postId)
                .orElseThrow(IllegalArgumentException::new);

        returnValue.setPostName(postResource.getPostName());
        returnValue.setDescription(postResource.getDescription());
        returnValue.setModifiedAt(LocalDateTime.now());

        return PostResponse.builder()
                .postId(returnValue.getPostId())
                .postName(returnValue.getPostName())
                .description(returnValue.getDescription())
                .createdAt(returnValue.getCreatedAt())
                .modifiedAt(returnValue.getModifiedAt()).build();
    }
}
