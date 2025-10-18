package com.solpyra.domain.wallet.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.solpyra.configuration.RabbitQueuesProperties;
import com.solpyra.configuration.RabbitQueuesProperties.QueueProperties;
import com.solpyra.domain.wallet.dto.CommissionMessage;
import com.solpyra.domain.wallet.services.WalletService;
import com.solpyra.rabbitmq.MessageProducer;
import jakarta.persistence.OptimisticLockException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommissionConsumer {

  private final WalletService walletService;
  private final ObjectMapper objectMapper;
  private final RabbitQueuesProperties rabbitQueuesProperties;
  private final MessageProducer messageProducer;

  @RabbitListener(queues = "${rabbitmq.queues.commission-clac.name}")
  public void receiveCommission(Message message, Channel channel) throws IOException {
    long tag = message.getMessageProperties().getDeliveryTag();
    String messageId = message.getMessageProperties().getMessageId();
    CommissionMessage commissionMessage = new CommissionMessage();
    int retryCount = (Integer) message.getMessageProperties()
        .getHeaders()
        .getOrDefault("x-retry-count", 0);

    try {
      log.info("Received commission message: {}", message);
      commissionMessage = objectMapper.readValue(message.getBody(), CommissionMessage.class);
      walletService.addCommission(commissionMessage);
    } catch (Exception e) {
      log.error("Error processing commission for order {}: {}", commissionMessage.getId(),
          e.getMessage(), e);
      log.info("Sending retry message {} ", messageId);
      QueueProperties retry = rabbitQueuesProperties.getCommissionClacRetry();
      messageProducer.send(retry.getExchange(), retry.getRoutingKey(), retryCount + 1, message);
    } finally {
      channel.basicAck(tag, false);
    }
  }
}