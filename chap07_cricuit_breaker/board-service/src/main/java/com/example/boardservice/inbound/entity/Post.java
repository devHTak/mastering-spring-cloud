package com.example.boardservice.inbound.entity;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity @Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Post {

    @Id @GeneratedValue
    private Long id;

    private String postId;

    private String userId;

    private String postName;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime modifiedAt;
}
