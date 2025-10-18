package com.solpyra.entities;

import com.solpyra.constant.CommissionStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;
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
public class CommissionLog extends BaseEntity {

  @Column(name = "order_id", nullable = false)
  private BigInteger orderId;

  @Column(name = "wallet_id", insertable = false, updatable = false)
  private BigInteger walletId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "wallet_id", nullable = false)
  private Wallet wallet;

  @Column(name = "commission_amount", nullable = false, precision = 18, scale = 2)
  private BigDecimal commissionAmount;

  @Column(name = "status", nullable = false, length = 20)
  private CommissionStatus status = CommissionStatus.SUCCESS; // SUCCESS / FAILED

  @Column(name = "processed_at", nullable = false)
  private ZonedDateTime processedAt = ZonedDateTime.now();
}