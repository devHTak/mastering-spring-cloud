package com.example.boardservice.outbound.dto;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class PostResource {
    private String postId;

    private String postName;

    private String description;
}
