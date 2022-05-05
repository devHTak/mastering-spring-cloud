package com.example.boardservice.inbound.repository;

import com.example.boardservice.inbound.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findByUserId(String userId);

    Optional<Post> findByUserIdAndPostId(String userId, String postId);
}
