package com.solpyra.entities;

import com.solpyra.constant.PayoutCycleStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.persistence.Version;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "payout_cycle",
    uniqueConstraints = @UniqueConstraint(name = "uq_payout_cycle_ym", columnNames = {"period_year", "period_month"})
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayoutCycle extends BaseEntity {

  @Column(name = "wallet_id", insertable = false, updatable = false)
  private BigInteger walletId;

  @Column(name = "period_year", nullable = false)
  private Integer periodYear;

  @Column(name = "period_month", nullable = false)
  private Integer periodMonth;

  @Column(name = "start_at", nullable = false)
  private ZonedDateTime startAt;

  @Column(name = "end_at", nullable = false)
  private ZonedDateTime endAt;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false)
  private PayoutCycleStatus status = PayoutCycleStatus.OPEN;

  @Column(name = "closed_at")
  private ZonedDateTime closedAt;

  @Column(name = "paid_image", length = 1000)
  private String paidImage;

  @Column(name = "created_at", nullable = false)
  private ZonedDateTime createdAt = ZonedDateTime.now();

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  private Wallet wallet;
}