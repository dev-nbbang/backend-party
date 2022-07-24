package com.dev.nbbang.party.global.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQProducerConfig {
    private final String NOTIFY_EXCHANGE = "notify.exchange";       // 알림 보내는 경우 요청이 많은 것을 고려해서 새로운 Exchange로 사용
    private final String NOTIFY_QUEUE = "notify.queue";
    private final String NOTIFY_ROUTING_KEY = "notify.route";

    @Bean
    public Queue queue() {
        return new Queue(NOTIFY_QUEUE, true);
    }

    @Bean
    public DirectExchange directExchange() {
        return new DirectExchange(NOTIFY_EXCHANGE);
    }

    @Bean
    public Binding bind(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(NOTIFY_ROUTING_KEY);
    }

    @Bean
    public RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory) {
        final RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());

        return rabbitTemplate;
    }
}
