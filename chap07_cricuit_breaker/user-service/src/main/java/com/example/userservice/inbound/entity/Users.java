package com.example.userservice.inbound.entity;

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
public class Users {

    @Id @GeneratedValue
    private Long id;

    private String userId;

    private String userName;

    private LocalDateTime createdAt;
}
