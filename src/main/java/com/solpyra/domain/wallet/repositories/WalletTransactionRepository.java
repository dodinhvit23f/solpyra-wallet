package com.solpyra.domain.wallet.repositories;

import com.solpyra.entities.WalletTransaction;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface WalletTransactionRepository extends JpaRepository<WalletTransaction, BigInteger> {

    @Query("""
        SELECT SUM(wt.amount)
        FROM Wallet w
        JOIN WalletTransaction wt ON wt.walletId = w.id
        WHERE w.customerId = :customerId AND
              wt.transactionType = com.solpyra.constant.TransactionType.COMMISSION AND
              wt.createdDate <= :endDate AND
              wt.createdDate >= :startDate
        """)
    BigDecimal getCommissionByTime(BigInteger customerId, ZonedDateTime startDate, ZonedDateTime endDate);
}
