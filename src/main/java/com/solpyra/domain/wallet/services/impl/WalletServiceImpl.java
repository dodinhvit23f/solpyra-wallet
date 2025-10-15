package com.solpyra.domain.wallet.services.impl;

import com.solpyra.constant.Constant;
import com.solpyra.constant.TransactionType;
import com.solpyra.domain.wallet.dto.CommissionMessage;
import com.solpyra.domain.wallet.repositories.CommissionLogRepository;
import com.solpyra.domain.wallet.repositories.WalletRepository;
import com.solpyra.domain.wallet.repositories.WalletTransactionRepository;
import com.solpyra.domain.wallet.services.WalletService;
import com.solpyra.entities.CommissionLog;
import com.solpyra.entities.Wallet;
import com.solpyra.entities.WalletTransaction;
import java.math.BigDecimal;
import java.util.LinkedList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class WalletServiceImpl implements WalletService {

  private final WalletRepository walletRepository;
  private final WalletTransactionRepository transactionRepository;
  private final CommissionLogRepository commissionLogRepository;

  /**
   * Safely add commission to user's wallet (Idempotent: won't process same order twice)
   */
  @Transactional
  @Override
  public void addCommission(CommissionMessage message) {

    Wallet wallet = walletRepository.findByCustomerIdForUpdate(message.getUserId())
        .orElseGet(() -> walletRepository.save(
            Wallet.builder()
                .customerId(message.getUserId())
                .balance(BigDecimal.ZERO)
                .withdrawBalance(BigDecimal.ZERO)
                .commissionedBalance(BigDecimal.ZERO)
                .build()
        ));

    List<WalletTransaction> transactions = new LinkedList<>();
    List<CommissionLog> logs = new LinkedList<>();

    message.getOrders().forEach(order -> {
      if (commissionLogRepository.findByOrderId(order.getOrderId()).isPresent()) {
        return;
      }

      wallet.setBalance(wallet.getBalance().add(order.getCommissionAmount()));

      transactions.add(WalletTransaction.builder()
          .wallet(wallet)
          .amount(order.getCommissionAmount())
          .transactionType(TransactionType.COMMISSION)
          .referenceId(order.getOrderId())
          .build());

      logs.add(CommissionLog.builder()
          .orderId(order.getOrderId())
          .wallet(wallet)
          .commissionAmount(order.getCommissionAmount())
          .status(Constant.SUCCESS)
          .build());
    });


    // Save triggers version increment; optimistic lock ensures no lost update
    walletRepository.save(wallet);
    transactionRepository.saveAll(transactions);
    commissionLogRepository.saveAll(logs);
  }
}