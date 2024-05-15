package com.netflix.zuulservice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Control {

    @GetMapping("/zuul")
    public String test() {
        return "";
    }
}
