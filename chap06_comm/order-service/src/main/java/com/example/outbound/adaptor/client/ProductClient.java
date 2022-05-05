package com.example.outbound.adaptor.client;

import com.example.outbound.dto.OrderResource;
import com.example.outbound.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(value="product-service")
public interface ProductClient {

    @GetMapping(value = "/products", produces = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<List<ProductResponse>> getProductsByProductIds(OrderResource orderResource);
}
