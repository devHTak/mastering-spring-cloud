package com.example.rabbitmqservice.client.consumer;

import com.example.rabbitmqservice.dto.TestRequest;
import com.example.rabbitmqservice.dto.TestResponse;
import com.example.rabbitmqservice.entity.Test;
import com.example.rabbitmqservice.repository.TestRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestConsumer {

    private final TestRepository repository;

    @Autowired
    public TestConsumer(TestRepository repository) {
        this.repository = repository;
    }

    @RabbitListener(queues = "spring-boot")
    public void receiveMessage(TestRequest testRequest) {
        System.out.println("TEST consumer: " + testRequest.getMessage());

        Test entity = new Test();
        entity.setMessage(testRequest.getMessage());

        repository.save(entity);
    }
}
