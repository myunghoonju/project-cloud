package com.mycloud.gateway;

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import org.springframework.cloud.circuitbreaker.resilience4j.ReactiveResilience4JCircuitBreakerFactory;
import org.springframework.cloud.circuitbreaker.resilience4j.Resilience4JConfigBuilder;
import org.springframework.cloud.client.circuitbreaker.Customizer;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.UriSpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import java.time.Duration;
import java.util.function.Function;

@Configuration
public class FilterConfig {

    @Bean
    public RouteLocator firstRouteLocator(RouteLocatorBuilder builder,
                                          FirstGatewayFilterFactory firstFilter) {
        return builder.routes()
                      .route("user-application", it -> it.path("/user-application/welcome")
                                                            .filters(first(firstFilter))
                                                            .uri("http://localhost:8989"))
                      .build();

    }

    @Bean
    public Customizer<ReactiveResilience4JCircuitBreakerFactory> defaultCustomizer() {
        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
                .circuitBreakerConfig(CircuitBreakerConfig.custom().failureRateThreshold(50)
                        .waitDurationInOpenState(Duration.ofSeconds(10))
                        .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                        .slidingWindowSize(2)
                        .build())
                .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofSeconds(1)).build())
                .build());
    }

    @Bean
    public RouteLocator secondRouteLocator(RouteLocatorBuilder builder,
                                           SecondGatewayFilterFactory secondFilter,
                                           LogFilterGatewayFilterFactory logFilter) {
        return builder.routes()
                      .route(it -> it.path("/user-application/welcome2")
                                     .filters(second(secondFilter, logFilter))
                                     .uri("http://localhost:8989"))
                      .build();

    }

    private Function<GatewayFilterSpec, UriSpec> first(FirstGatewayFilterFactory firstGatewayFilterFactory) {
        return config -> config.rewritePath("/user-application/(?<segment>.*)", "/${segment}")
                               .addRequestParameter("posType", "pos")
                               .addRequestHeader("first-req", "first-req-val")
                               .filter(firstGatewayFilterFactory.apply(g -> g.setMsg("msg").setPreLog(true).setPostLog(true)));
    }

    private Function<GatewayFilterSpec, UriSpec> second(SecondGatewayFilterFactory secondFilter, LogFilterGatewayFilterFactory logFilter) {
        return config -> config.rewritePath("/user-application/(?<segment>.*)", "/${segment}")
                               .addRequestParameter("posType", "pos")
                               .filters(secondFilter.apply(s -> s.setMethod(HttpMethod.GET)),
                                        logFilter.apply(l -> l.setPreLog(true).setPostLog(true).setMsg("hi")));
    }
}
