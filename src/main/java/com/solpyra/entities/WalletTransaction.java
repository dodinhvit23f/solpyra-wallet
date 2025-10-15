package com.solpyra.entities;

import com.solpyra.constant.TransactionType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "wallet_transaction")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class WalletTransaction extends LogEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "wallet_id", nullable = false)
  private Wallet wallet;

  @Column(name = "wallet_id", insertable = false, updatable = false)
  private BigInteger walletId;

  @Column(name = "amount", nullable = false, precision = 18, scale = 2)
  private BigDecimal amount;

  @Column(name = "transaction_type", nullable = false, length = 30)
  @Enumerated(EnumType.ORDINAL)
  private TransactionType transactionType; // e.g. COMMISSION, WITHDRAW

  @Column(name = "reference_id")
  private BigInteger referenceId;

  @Column(name = "payment_image")
  private String paymentImage;

}