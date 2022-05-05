package com.example.userservice.outbound.adaptor.client;

import com.example.userservice.outbound.dto.PostResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name="BOARD-SERVICE")
public interface BoardClient {
    @GetMapping("/users/{userId}/posts")
    ResponseEntity<List<PostResponse>> getPostsByUserId(@PathVariable  String userId);
}
