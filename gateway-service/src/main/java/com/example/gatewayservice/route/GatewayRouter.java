package com.example.gatewayservice.route;

import com.example.gatewayservice.filter.GlobalLoggingFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayRouter {

    @Bean
    public RouteLocator route(RouteLocatorBuilder builder, GlobalLoggingFilter globalLoggingFilter) {
        return builder.routes()
                .route(r -> {
                    return r.path("/product-service/**")
                            .filters(f-> f.filter(globalLoggingFilter.apply(new GlobalLoggingFilter.Config(true, true)))
                                    .rewritePath("/product-service/(?<segment>/*)", "/$\\{segment}" ))
                            .uri("lb://PRODUCT-SERVICE");
                })
                .route(r -> {
                    return r.path("/order-service/**")
                            .filters(f-> f.filter(globalLoggingFilter.apply(new GlobalLoggingFilter.Config(true, true)))
                                    .rewritePath("/order-service/(?<segment>/*)", "/$\\{segment}" ))
                            .uri("lb://ORDER-SERVICE");
                })
                .route(r -> {
                    return r.path("/board-service/**")
                            .filters(f -> f.filter(globalLoggingFilter.apply(new GlobalLoggingFilter.Config(true, true)))
                                    .rewritePath("/board-service/(?<segment>/*)", "/$\\{segment}"))
                            .uri("lb://BOARD-SERVICE");
                })
                .route(r -> {
                    return r.path("/user-service/**")
                            .filters(f -> f.filter(globalLoggingFilter.apply(new GlobalLoggingFilter.Config(true, true)))
                                    .rewritePath("/user-service/(?<segment>/*)", "/$\\{segment}"))
                            .uri("lb://USER-SERVICE");
                })
                .build();
    }
}
