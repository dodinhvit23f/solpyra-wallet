package com.solpyra.domain.wallet.repositories;


import com.solpyra.entities.PayoutCycle;
import java.math.BigInteger;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PayoutCycleRepository extends JpaRepository<PayoutCycle, BigInteger> {

  Optional<PayoutCycle> findByWalletIdAndPeriodYearAndPeriodMonth(
      BigInteger walletId, int year, int month
  );

  boolean existsByWalletIdAndPeriodYearAndPeriodMonth(
      BigInteger walletId, int year, int month
  );
}