package com.example.boardservice.outbound.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data @Builder
public class PostResponse {
    private String postId;

    private String postName;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;
}
