package com.example.rabbitmqservice.controller;

import com.example.rabbitmqservice.client.producer.TestProducer;
import com.example.rabbitmqservice.dto.TestRequest;
import com.example.rabbitmqservice.dto.TestResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final TestProducer testProducer;

    @Autowired
    public TestController(TestProducer testProducer) {
        this.testProducer = testProducer;
    }

    @PostMapping("/tests")
    public ResponseEntity<TestResponse> createTest(@RequestBody TestRequest testRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(testProducer.produceTest(testRequest));
    }
}
