package com.example.userservice.inbound.service;

import com.example.userservice.inbound.entity.Users;
import com.example.userservice.inbound.repository.UserRepository;
import com.example.userservice.outbound.adaptor.client.BoardClient;
import com.example.userservice.outbound.dto.UserResource;
import com.example.userservice.outbound.dto.UserResponse;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BoardClient boardClient;

    public UserResponse getUserByUserId(String userId) {
        Users user = userRepository.findByUserId(userId)
                .orElseThrow(IllegalArgumentException::new);

        return UserResponse.builder()
                    .userId(user.getUserId())
                    .userName(user.getUserName())
                    .createdAt(user.getCreatedAt())
                    .posts(boardClient.getPostsByUserId(userId).getBody())
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
