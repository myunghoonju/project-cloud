package com.app.client;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.app.client.domain.UserService;
import com.app.client.domain.UserVO;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class ClientController {

    private final Environment env;
    private final UserService service;

    @Value("${info.message}")
    private String msg;

    @GetMapping("/health")
    public String health() {
        String secret = env.getProperty("token.secret");
        return "health " + msg + " secret " + secret;
    }

    @GetMapping("/welcome")
    public String welcome(@RequestHeader("first-req") String firstReq) {

        System.err.println(firstReq);
        return "fist-service - " + env.getProperty("local.server.port");
    }

    @PostMapping("/login")
    public ResponseEntity<UserVO> login(@RequestBody UserVO vo) {
        return ResponseEntity.status(HttpStatus.OK).body(vo);
    }

    @PostMapping("/user")
    public ResponseEntity<UserVO> save(@RequestBody @Valid UserVO vo) {
        service.save(vo);
        return ResponseEntity.status(HttpStatus.CREATED).body(vo.toRes());
    }

    @GetMapping("/user/{id}")
    public ResponseEntity<UserVO> user(@PathVariable Long id) {
        return ResponseEntity.ok().body(service.user(id));
    }
}
