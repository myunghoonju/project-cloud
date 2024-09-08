package com.mycloud.gateway;

import org.springframework.cloud.gateway.filter.factory.rewrite.RewriteFunction;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.UriSpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import java.util.List;
import java.util.function.Function;

@Configuration
public class FilterConfig {

    @Bean
    public RouteLocator firstRouteLocator(RouteLocatorBuilder builder,
                                          FirstGatewayFilterFactory firstFilter) {
        return builder.routes()
                      .route("user-application", it -> it.path("/client-one/**")
                                                            .filters(first(firstFilter))
                                                            .uri("http://localhost:8989"))
                      .build();

    }

//    @Bean
//    public Customizer<Resilience4JCircuitBreakerFactory> simpleCircuitBreaker() {
//        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder(id)
//                .circuitBreakerConfig(CircuitBreakerConfig.custom().failureRateThreshold(50)
//                                                                   .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
//                        .minimumNumberOfCalls(2)
//                        .slidingWindowSize(10)
//                        .waitDurationInOpenState(Duration.ofSeconds(10))
//                        .failureRateThreshold(50)
//                        .slowCallDurationThreshold(Duration.ofSeconds(3))
//                        .slowCallRateThreshold(60)
//                        .permittedNumberOfCallsInHalfOpenState(5)
//                        .recordException(throwable -> throwable instanceof TimeoutException)
//                        .recordException(throwable -> throwable instanceof CallNotPermittedException)
//                        .automaticTransitionFromOpenToHalfOpenEnabled(true)
//                        .build())
//                .build());
//    }
//
//    @Bean
//    public Customizer<ReactiveResilience4JCircuitBreakerFactory> second() {
//        return factory -> factory.configureDefault(id -> new Resilience4JConfigBuilder("second")
//                .circuitBreakerConfig(CircuitBreakerConfig.ofDefaults())
//                .timeLimiterConfig(TimeLimiterConfig.custom().timeoutDuration(Duration.ofMillis(200)).build())
//                .build());
//    }

    @Bean
    public RouteLocator secondRouteLocator(RouteLocatorBuilder builder,
                                           SecondGatewayFilterFactory secondFilter,
                                           LogFilterGatewayFilterFactory logFilter) {
        return builder.routes()
                      .route(it -> it.path("/user-application/2/**")
                                     .filters(second(secondFilter, logFilter))
                                     .uri("http://localhost:8989"))
                      .build();

    }

    private Function<GatewayFilterSpec, UriSpec> first(FirstGatewayFilterFactory firstGatewayFilterFactory) {
        return config -> config.rewritePath("/client-one/(?<segment>.*)", "/${segment}")
                               .addRequestParameter("posType", "pos")
                               .addRequestHeader("first-req", "first-req-val")
                               .filter(firstGatewayFilterFactory.apply(g -> g.setMsg("msg").setPreLog(true).setPostLog(true)));
    }

    private Function<GatewayFilterSpec, UriSpec> second(SecondGatewayFilterFactory secondFilter, LogFilterGatewayFilterFactory logFilter) {
        return config -> config.rewritePath("/user-application/2/(?<segment>.*)", "/${segment}")
                               .addRequestParameter("posType", "pos")
                               .modifyResponseBody(Object.class, Object.class, ResponseBodyUtil::reWrite)
//                               .modifyResponseBody(List.class, List.class, reWriteFunction2())
                               .filters(secondFilter.apply(s -> s.setMethod(HttpMethod.GET)))
                               .circuitBreaker(it -> it.setName("simpleCircuitBreaker")
                                                       .setFallbackUri("forward:/fallback"));
    }

    private static RewriteFunction<List, List> reWriteFunction2() {
        return (exchange, list) -> ResponseBodyUtil.reWritea(list);
    }
}
