package com.mycloud.gateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.GatewayFilterSpec;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.cloud.gateway.route.builder.UriSpec;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Function;

@Configuration
public class FilterConfig {

    @Bean
    public RouteLocator routeLocator(RouteLocatorBuilder builder, GlobalFilter globalFilter) {
        return builder.routes()
                      .route("user-application", it -> it.path("/user-application/**")
                                                            .filters(userAppFilterConfig(globalFilter))
                                                            .uri("http://localhost:8989"))

                      .route(it -> it.path("/two/**")
                                     .filters(filter -> filter.addRequestHeader("second-req", "second-req-val")
                                                              .addResponseHeader("second-res-", "second-res-val"))
                                     .uri("http://localhost:8082"))
                      .build();

    }

    private Function<GatewayFilterSpec, UriSpec> userAppFilterConfig(GlobalFilter globalFilter) {
        return config -> config.rewritePath("/user-application/(?<segment>.*)", "/${segment}")
                               .addRequestHeader("first-req", "first-req-val")
                               .addResponseHeader("first-res-", "first-res-val")
                               .filter(globalFilter.apply(g -> g.setMsg("msg").setPreLog(true).setPostLog(true)));
    }

    public static void main(String[] args) {
        String s = "aaaa/v2/zip/1111".replaceAll("/v2/zip/(?<zipcode>.*)", "/api/zip/${zipcode}-");
        System.out.println(s);

    }
}
