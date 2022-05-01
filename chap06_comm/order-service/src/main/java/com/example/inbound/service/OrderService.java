package com.example.inbound.service;

import com.example.inbound.entity.Order;
import com.example.inbound.entity.OrderStatus;
import com.example.inbound.repository.OrderRepository;
import com.example.outbound.adaptor.client.ProductClient;
import com.example.outbound.dto.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductClient productClient;

    public List<Order> createOrder(OrderResource orderResource) throws JsonProcessingException {
        List<ProductResponse> products = productClient.getProductsByProductIds(orderResource.getProductIds());
        long price = products.stream().map(product -> product.getPrice())
                .reduce(0L, (prev, post) -> prev += post);

        return orderRepository.saveAll(Arrays.stream(orderResource.getProductIds())
                .map(productId -> {
                    return Order.builder()
                            .orderId(UUID.randomUUID().toString())
                            .orderStatus(OrderStatus.ACCEPTED)
                            .productId(productId)
                            .price(price)
                            .build();
                }).collect(Collectors.toList()));
    }
}
