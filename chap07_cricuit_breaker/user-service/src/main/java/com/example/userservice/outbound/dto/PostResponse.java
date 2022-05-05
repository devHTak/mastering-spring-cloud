package com.example.userservice.outbound.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class PostResponse {

    private String postId;

    private String postName;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;
}
