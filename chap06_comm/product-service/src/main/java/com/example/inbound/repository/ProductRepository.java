package com.example.inbound.repository;

import com.example.inbound.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    @Query("select P from Product P where product_id in :ids")
    List<Product> findByAllProductIds(@Param("ids") String[] productIds);
}
