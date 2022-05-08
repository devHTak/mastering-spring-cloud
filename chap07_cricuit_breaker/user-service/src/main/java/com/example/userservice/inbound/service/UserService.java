package com.example.userservice.inbound.service;

import com.example.userservice.inbound.entity.Users;
import com.example.userservice.inbound.repository.UserRepository;
import com.example.userservice.outbound.adaptor.client.BoardClient;
import com.example.userservice.outbound.dto.PostResponse;
import com.example.userservice.outbound.dto.UserResource;
import com.example.userservice.outbound.dto.UserResponse;
import lombok.AllArgsConstructor;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreaker;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BoardClient boardClient;
    private final Resilience4JCircuitBreakerFactory circuitBreakerFactory;

    public UserResponse getUserByUserId(String userId) {
        Users user = userRepository.findByUserId(userId)
                .orElseThrow(IllegalArgumentException::new);

        // circuitbreaker 사용
        Resilience4JCircuitBreaker circuitBreaker = circuitBreakerFactory.create("board-service");
        List<PostResponse> posts = circuitBreaker.run(
                () -> boardClient.getPostsByUserId(userId).getBody(),
                (throwable) -> new ArrayList<>()
        );

        return UserResponse.builder()
                    .userId(user.getUserId())
                    .userName(user.getUserName())
                    .createdAt(user.getCreatedAt())
                    .posts(posts)
                    .build();
    }

    public UserResponse saveUser(UserResource userResource) {
        Users user = Users.builder()
                .userName(userResource.getUserName())
                .userId(UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .build();

        Users returnUser = userRepository.save(user);
        return UserResponse.builder()
                .userId(returnUser.getUserId())
                .createdAt(returnUser.getCreatedAt())
                .userName(returnUser.getUserName())
                .build();
    }
}
