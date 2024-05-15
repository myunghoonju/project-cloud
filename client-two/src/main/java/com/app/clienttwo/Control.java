package com.app.clienttwo;

import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/two")
@RestController
public class Control {

    private Environment env;

    public Control(Environment env) {
        this.env = env;
    }

    @GetMapping("/welcome")
    public String welcome(@RequestHeader("second-req") String secondReq) {
        System.err.println(secondReq);

        return "second-service - " + env.getProperty("local.server.port");
    }
}
