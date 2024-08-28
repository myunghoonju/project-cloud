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
                                     FirstGatewayFilterFactory firstFilter,
                                     SecondGatewayFilterFactory secondFilter,
                                     LogFilterGatewayFilterFactory logFilter) {
        return builder.routes()
                      .route("user-application", it -> it.path("/user-application/welcome")
                                                            .filters(first(firstFilter))
                                                            .uri("http://localhost:8989"))

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
