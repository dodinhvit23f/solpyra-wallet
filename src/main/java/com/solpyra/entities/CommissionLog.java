package com.solpyra.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
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
@Table(name = "commission_log")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class CommissionLog extends LogEntity {

  @Column(name = "order_id", nullable = false)
  private BigInteger orderId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "wallet_id", nullable = false)
  private Wallet wallet;

  @Column(name = "wallet_id", insertable = false, updatable = false)
  private BigInteger walletId;

  @Column(name = "commission_amount", nullable = false, precision = 18, scale = 2)
  private BigDecimal commissionAmount;

  @Column(name = "status", nullable = false, length = 20)
  private String status = "SUCCESS"; // SUCCESS / FAILED
}