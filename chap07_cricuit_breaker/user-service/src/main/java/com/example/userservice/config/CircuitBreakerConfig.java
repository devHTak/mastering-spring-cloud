package com.example.userservice.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class CircuitBreakerConfig {

    @Bean
    public Customizer<Resilience4JCircuitBreakerFactory> circuitBreakerConfig() {
        return resilience4JCircuitBreakerFactory -> {
          resilience4JCircuitBreakerFactory.configureDefault(id -> {
              return new Resilience4JConfigBuilder(id)
                      .timeLimiterConfig(timeLimiterConfig())
                      .circuitBreakerConfig(customizerCircuitBreakerConfig())
                      .build();
          });
        };
    }

    private TimeLimiterConfig timeLimiterConfig() {
        return TimeLimiterConfig.custom()
                // timeLimiter는 future supplier의 time limit을 정하는 API 기본 1초
                .timeoutDuration(Duration.ofSeconds(4))
                .build();
    }

    private io.github.resilience4j.circuitbreaker.CircuitBreakerConfig customizerCircuitBreakerConfig() {
        return io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.custom()
                // circuitBreaker 를 열지 결정하는 failure rate, 기본 50
                .failureRateThreshold(4f)
                // circuitBreaker를 open한 상태를 유지하는 지속 시간 이 기간 이후 half-open 상태 기본 60sec
                .waitDurationInOpenState(Duration.ofMillis(1000))
                // circuitBreaker 닫힐 때 통화 결과 기록하는 데 사용되는 슬라이딩 창의 유형 구성(카운트 또는 시간 기반)
                .slidingWindowType(io.github.resilience4j.circuitbreaker.CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                // circuitBreaker 닫힐 때 호출 결과를 기록하는 데 사용되는 슬라이딩 창의 크기 구성
                .slidingWindowSize(2)
                .build();
    }
}
