package com.mycloud.gateway;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class Consumer {

    private final CircuitBreakerRegistry circuitBreakerRegistry;

    @RabbitListener(queues = "#{blockingQueue.getName()}")
    public void consumeBlocking(Object payload) {
        log.info("name: {}", payload);
        CircuitBreaker simpleCircuitBreaker = circuitBreakerRegistry.circuitBreaker("simpleCircuitBreaker2");
        simpleCircuitBreaker.transitionToOpenState();
    }
//
//    @RabbitListener(queues = "#{blockingQueue.getName()}")
//    public void consumeBlocking(List<PayLoad> payload) {
//        payload.forEach(payLoad -> {
//            log.info("name: {}", payLoad.getName());
//            payLoad.getList().forEach(li -> log.info("list {}", li));
//        });
//    }

}

