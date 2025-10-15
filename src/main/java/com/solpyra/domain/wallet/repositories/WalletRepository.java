package com.solpyra.domain.wallet.repositories;

import com.solpyra.entities.Wallet;
import jakarta.persistence.LockModeType;
import java.math.BigInteger;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface WalletRepository extends JpaRepository<Wallet, BigInteger> {


  Optional<Wallet> findByCustomerId(BigInteger customerId);

  @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
  @Query("SELECT w FROM Wallet w WHERE w.customerId = :customerId")
  Optional<Wallet> findByCustomerIdForUpdate(@Param("customerId") BigInteger customerId);
}
