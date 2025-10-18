package com.solpyra.configuration;


import com.solpyra.configuration.RabbitQueuesProperties.QueueProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class RabbitMQConfiguration {

  final RabbitQueuesProperties rabbitQueuesProperties;

  @Bean
  public Queue commissionClacQueue() {
    QueueProperties props = rabbitQueuesProperties.getCommissionClac();
    return QueueBuilder.durable(props.getName())
        .deadLetterExchange("")
        .deadLetterRoutingKey(rabbitQueuesProperties.getCommissionClac().getName())
        .lazy() // Optional, good for low-memory server
        .build();
  }

  @Bean
  public Queue retryCommissionClacQueue() {
    QueueProperties props = rabbitQueuesProperties.getCommissionClacCallback();
    return QueueBuilder.durable(props.getName())
        .deadLetterExchange("")
        .deadLetterRoutingKey(rabbitQueuesProperties.getCommissionClac().getName())
        .ttl(props.getTtl())
        .lazy() // Optional, good for low-memory server
        .build();
  }

  @Bean
  public TopicExchange addOrderExchange() {
    QueueProperties props = rabbitQueuesProperties.getCommissionClac();
    return new TopicExchange(props.getExchange());
  }

  @Bean
  public Binding addOrderBinding(Queue commissionClacQueue, TopicExchange addOrderExchange) {
    var props = rabbitQueuesProperties.getCommissionClac();
    return BindingBuilder
        .bind(commissionClacQueue)
        .to(addOrderExchange)
        .with(props.getRoutingKey());
  }

  @Bean
  public Binding retryAddOrderBinding(Queue retryCommissionClacQueue, TopicExchange addOrderExchange) {
    return BindingBuilder.bind(retryCommissionClacQueue)
        .to(addOrderExchange)
        .with(rabbitQueuesProperties.getCommissionClacCallback().getRoutingKey());
  }


}