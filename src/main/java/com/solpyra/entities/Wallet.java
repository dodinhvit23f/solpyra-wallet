package com.solpyra.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.math.BigInteger;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "wallet")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Wallet extends LogEntity {

  @Column(name = "customer_id", nullable = false, length = 50)
  private BigInteger customerId;

  @Column(name = "balance", nullable = false)
  private BigDecimal balance = BigDecimal.ZERO;

  @Column(name = "withdraw_balance", nullable = false)
  private BigDecimal withdrawBalance = BigDecimal.ZERO;

  @Column(name = "commissioned_balance", nullable = false)
  private BigDecimal commissionedBalance = BigDecimal.ZERO;
}