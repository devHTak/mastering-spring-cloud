package com.example.outbound.adaptor.client;

import com.example.outbound.dto.ProductResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(value="PRODUCT-SERVICE")
public interface ProductClient {

    @GetMapping(value = "/products", produces = "application/json")
    List<ProductResponse> getProductsByProductIds(String[] productIds);
}
