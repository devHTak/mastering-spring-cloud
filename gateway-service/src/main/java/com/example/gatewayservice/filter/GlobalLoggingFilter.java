package com.example.gatewayservice.filter;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.factory.AbstractGatewayFilterFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@Getter
public class GlobalLoggingFilter extends AbstractGatewayFilterFactory<GlobalLoggingFilter.Config> {

    @Override
    public GatewayFilter apply(Config config) {
        return (exchange, chain) -> {
            if(config.isPreLogger())
                log.info("Gateway Pre Logger: requestId - {}", exchange.getRequest().getId());

            return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                if(config.isPostLogger())
                    log.info("Gateway Post Logger: response status - {}", exchange.getResponse().getStatusCode());
            }));
        };
    }

    @Getter @Setter
    @NoArgsConstructor @AllArgsConstructor
    public static class Config {
        private boolean preLogger;
        private boolean postLogger;
    }
}
