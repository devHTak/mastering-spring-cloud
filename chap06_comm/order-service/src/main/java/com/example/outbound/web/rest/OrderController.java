package com.example.outbound.web.rest;

import com.example.inbound.entity.Order;
import com.example.inbound.service.OrderService;
import com.example.outbound.dto.OrderResource;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/orders")
    public ResponseEntity<List<Order>> prepare(@RequestBody OrderResource orderResource) throws JsonProcessingException {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(orderService.createOrder(orderResource));
    }
}
