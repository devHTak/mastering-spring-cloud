package com.example.rabbitmqservice.repository;

import com.example.rabbitmqservice.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TestRepository extends JpaRepository<Test, Long> {
}
