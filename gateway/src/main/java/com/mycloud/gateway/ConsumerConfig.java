package com.mycloud.gateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Declarable;
import org.springframework.amqp.core.Declarables;
import org.springframework.amqp.core.ExchangeBuilder;
import org.springframework.amqp.core.FanoutExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.UUID;

import static org.springframework.amqp.rabbit.connection.CachingConnectionFactory.ConfirmType.SIMPLE;

@Slf4j
@Configuration
public class ConsumerConfig {

    @Bean
    public ConnectionFactory rabbitCon() {
        CachingConnectionFactory conn = new CachingConnectionFactory();
        conn.setHost("localhost");
        conn.setPort(5672);
        conn.setUsername("guest");
        conn.setPassword("guest");
        conn.setPublisherConfirmType(SIMPLE);
        return conn;
    }

    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate firstRabbit = new RabbitTemplate(rabbitCon());
        firstRabbit.setMessageConverter(converter());
        firstRabbit.setReceiveTimeout(1000);

        return firstRabbit;
    }

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

    @Bean
    public AmqpAdmin rabbitAmqpAdmin() {
        return new RabbitAdmin(rabbitTemplate());
    }

    /** blockingQueue */
    @Bean
    public Queue blockingQueue() {
        return QueueBuilder.durable(UUID.randomUUID().toString()).quorum().build();
    }

    @Bean
    public FanoutExchange fanoutExchange(AmqpAdmin rabbitAmqpAdmin) {
        FanoutExchange fanoutExchange = ExchangeBuilder.fanoutExchange("x.test-fanout").build();
        fanoutExchange.setAdminsThatShouldDeclare(rabbitAmqpAdmin);

        return fanoutExchange;
    }

    @Bean
    public Declarables fanoutBindings(AmqpAdmin rabbitAmqpAdmin,
                                      Queue blockingQueue,
                                      FanoutExchange fanoutExchange) {
        return new Declarables(getDeclarable(rabbitAmqpAdmin, blockingQueue, fanoutExchange));
    }

    private Declarable getDeclarable(AmqpAdmin amqpAdmin,
                                     Queue queue,
                                     FanoutExchange exchange) {
        Binding binding = BindingBuilder.bind(queue).to(exchange);
        binding.setAdminsThatShouldDeclare(amqpAdmin);

        return binding;
    }
}
