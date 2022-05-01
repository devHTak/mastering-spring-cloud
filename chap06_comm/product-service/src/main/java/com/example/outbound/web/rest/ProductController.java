package com.example.outbound.web.rest;

import com.example.inbound.entity.Product;
import com.example.inbound.repository.ProductRepository;
import com.example.outbound.dto.ProductResource;
import com.example.outbound.dto.ProductResponse;
import com.example.util.CommonConverterUtil;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ProductController {

    private final ProductRepository productRepository;
    private final CommonConverterUtil commonConverterUtil;

    @GetMapping("/products")
    public List<ProductResponse> getProductsByProductIds(@RequestBody String[] productIds) {
        List<Product> products = productRepository.findByAllProductIds(productIds);
        return products.stream().map(product -> {
                    ProductResponse response = null;
                    try {
                        response = (ProductResponse) commonConverterUtil.converter(product, ProductResponse.class);
                    } catch(JsonProcessingException e) {
                        log.error(e.getMessage());
                    }
                    return response;
                }).collect(Collectors.toList());
    }

    @PostMapping("/products")
    public ProductResponse saveProduct(@RequestBody ProductResource productResource) throws JsonProcessingException {
        Product product = (Product) commonConverterUtil.converter(productResource, Product.class);
        product.setProductId(UUID.randomUUID().toString());

        Product returnProduct = productRepository.save(product);
        return (ProductResponse) commonConverterUtil.converter(returnProduct, ProductResponse.class);
    }


}
