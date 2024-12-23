package com.app.clienttwo;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class Control {

    private final Environment env;

    private final RetryService retryService;
    private final CircuitBreakerService circuitBreakerService;

    @GetMapping("/health")
    public String health() {
        return "two";
    }


    @GetMapping("/welcome")
    public String welcome(@RequestHeader("second-req") String secondReq) {
        System.err.println(secondReq);

        return "second-service - " + env.getProperty("local.server.port");
    }

    @GetMapping("/test")
    public String welcome() {
        return "second-service - " + env.getProperty("local.server.port");
    }


    @GetMapping("/api-call")
    public String apiCall(@RequestParam String param) {
        return retryService.process(param);
    }

    @GetMapping("/call")
    public String call(@RequestParam String param) throws InterruptedException {
        return circuitBreakerService.process(param);
    }
}
