package com.mycloud.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.UriSpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;

import java.util.function.Function;

@Configuration
public class FilterConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder,
                                     GlobalFilter globalFilter,
                                     SecondGatewayFilterFactory secondFilter,
                                     LogFilterGatewayFilterFactory logFilter) {
        return builder.routes()
                      .route("user-application", it -> it.path("/user-application/welcome")
                                                            .filters(first(globalFilter))
                                                            .uri("http://localhost:8989"))

                      .route(it -> it.path("/user-application/welcome2")
                                     .filters(second(secondFilter, logFilter))
                                     .uri("http://localhost:8989"))
                      .build();

    }

    private Function<GatewayFilterSpec, UriSpec> first(GlobalFilter globalFilter) {
        return config -> config.rewritePath("/user-application/(?<segment>.*)", "/${segment}")
                               .addRequestHeader("first-req", "first-req-val")
                               .addResponseHeader("first-res-", "first-res-val")
                               .filter(globalFilter.apply(g -> g.setMsg("msg").setPreLog(true).setPostLog(true)));
    }

    private Function<GatewayFilterSpec, UriSpec> second(SecondGatewayFilterFactory secondFilter, LogFilterGatewayFilterFactory logFilter) {
        return config -> config.rewritePath("/user-application/(?<segment>.*)", "/${segment}")
                               .filters(secondFilter.apply(s -> s.setMethod(HttpMethod.GET)),
                                        logFilter.apply(l -> l.setPreLog(true).setPostLog(true).setMsg("hi")));
    }
}
