package com.solpyra.domain.wallet.repositories;

import com.solpyra.entities.CommissionLog;
import java.math.BigInteger;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommissionLogRepository extends JpaRepository<CommissionLog, BigInteger> {

  Optional<CommissionLog> findByOrderId(BigInteger orderId);
}
