package com.solpyra.domain.wallet.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.solpyra.configuration.RabbitQueuesProperties;
import com.solpyra.configuration.RabbitQueuesProperties.QueueProperties;
import com.solpyra.constant.Constant;
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

    QueueProperties callbackQueue = rabbitQueuesProperties.getCommissionClacCallback();

    try {
      log.info("Received commission message: {}, {} times", messageId, retryCount + 1);
      commissionMessage = objectMapper.readValue(message.getBody(), CommissionMessage.class);
      walletService.addCommission(commissionMessage);
      commissionMessage.setStatus(Constant.SUCCESS);
    } catch (Exception e) {
      log.error("Error processing commission for order {}: {}", commissionMessage.getId(),
          e.getMessage(), e);
      log.error("Sending retry message {} - {}", messageId, retryCount);
      commissionMessage.setStatus(Constant.FAILED);
      commissionMessage.setErrorMessage(e.getMessage());
    } finally {
      messageProducer.send(callbackQueue.getExchange(), callbackQueue.getName(), commissionMessage);
      channel.basicAck(tag, false);
    }
  }
}