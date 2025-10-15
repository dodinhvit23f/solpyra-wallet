package com.solpyra.domain.wallet.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import com.solpyra.domain.wallet.dto.CommissionMessage;
import com.solpyra.domain.wallet.services.WalletService;
import jakarta.persistence.OptimisticLockException;
import java.io.IOException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class CommissionConsumer {

  private final WalletService walletService;
  private final ObjectMapper objectMapper;

  @RabbitListener(queues = "commission.queue")
  public void receiveCommission(Message message, Channel channel) throws IOException {
    long tag = message.getMessageProperties().getDeliveryTag();
    CommissionMessage commissionMessage = new CommissionMessage();
    try {
      log.info("Received commission message: {}", message);
      commissionMessage =  objectMapper.readValue(message.getBody(), CommissionMessage.class);
      walletService.addCommission(commissionMessage);
    } catch (OptimisticLockException e) {
      log.error("Error processing commission for order {}: {}", commissionMessage.getId(),
          e.getMessage(), e);
      throw e; // Let RabbitMQ handle retry or DLQ
    } catch (Exception e) {
      log.error("Unexpect error with commission calculator",e);
    } finally {
      channel.basicAck(tag, false);
    }
  }
}