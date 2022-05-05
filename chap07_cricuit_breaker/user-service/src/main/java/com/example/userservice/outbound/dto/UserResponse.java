package com.example.userservice.outbound.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data @Builder
public class UserResponse {

    private String userId;

    private String userName;

    private LocalDateTime createdAt;

    private List<PostResponse> posts;
}
