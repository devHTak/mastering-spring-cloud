package com.example.userservice.outbound.web.rest;

import com.example.userservice.inbound.service.UserService;
import com.example.userservice.outbound.dto.UserResource;
import com.example.userservice.outbound.dto.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users/{userId}")
    public ResponseEntity<UserResponse> getUserByid(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserByUserId(userId));
    }

    @PostMapping("/users")
    public ResponseEntity<UserResponse> saveUser(@RequestBody UserResource userResource) {
        return ResponseEntity.ok(userService.saveUser(userResource));
    }



}
