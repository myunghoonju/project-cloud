package com.app.client;

import com.app.client.domain.UserService;
import com.app.client.domain.UserVO;
import com.app.client.dto.WelcomeDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Slf4j
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
        String env = this.env.getProperty("env");
        return "health " + msg + " secret " + secret + "\n env " + env;
    }

    @GetMapping("/welcome")
    public String welcome(@RequestParam(name = "posType") String type) {
        throw new RuntimeException();
    }

    @GetMapping("/welcome2/aaa")
    public List<WelcomeDto> welcomeaaa(@RequestHeader HttpHeaders headers) {
        log.info("sleuth 1 {}", headers);
        ResponseEntity<String> exchange = new RestTemplate().exchange("http://localhost:8888/test", HttpMethod.GET, null, String.class);
        log.info("sleuth 2 {}", exchange.getBody());
        return List.of(new WelcomeDto("TEST", "PASSWORD"));
    }

    @GetMapping("/welcome2")
    public WelcomeDto welcome2() {
        return new WelcomeDto("TEST", "PASSWORD");
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
