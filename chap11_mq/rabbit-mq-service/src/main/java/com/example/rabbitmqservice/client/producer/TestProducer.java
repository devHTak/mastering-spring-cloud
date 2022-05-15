package com.example.rabbitmqservice.client.producer;

import com.example.rabbitmqservice.dto.TestRequest;
import com.example.rabbitmqservice.dto.TestResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TestProducer {

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public TestProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public TestResponse produceTest(TestRequest testRequest) {
        System.out.println("TEST PRODUCER: " + testRequest.getMessage());
        TestResponse response = new TestResponse();
        response.setMessage(testRequest.getMessage());

        try {
            rabbitTemplate.convertAndSend("spring-boot-exchange", "foo.bar.baz", testRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return response;
    }
}
