package com.mycloud.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 *   cloud:
 *     gateway:
 *       routes:
 *         - id: first-service
 *           uri: http://localhost:8081
 *           predicates:
 *             - Path=/one/**
 *           filters:
 *             - AddRequestHeader=first-req, first-req-val
 *             - AddResponseHeader=first-res, first-res-val
 *         - id: second-service
 *           uri: http://localhost:8082
 *           predicates:
 *             - Path=/two/**
 *           filters:
 *             - AddRequestHeader=second-req, second-req-val
 *             - AddResponseHeader=second-res, second-res-val
 * */
//@Configuration
public class FilterConfig {

//    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                      .route(it -> it.path("/one/**")
                                     .filters(filter -> filter.addRequestHeader("first-req", "first-req-val")
                                                              .addResponseHeader("first-res-", "first-res-val"))
                                     .uri("http://localhost:8081"))
                      .route(it -> it.path("/two/**")
                                     .filters(filter -> filter.addRequestHeader("second-req", "second-req-val")
                                                              .addResponseHeader("second-res-", "second-res-val"))
                                     .uri("http://localhost:8082"))
                      .build();
    }
}
